/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2018 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.alta.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TemplateCompiler<C> {
    private Map<String,PropertyGroup<C,?>> propertyGroups = new HashMap<String,PropertyGroup<C,?>>();
    private PropertyGroup<C,?> defaultPropertyGroup;
    
    public void addPropertyGroup(String name, PropertyGroup<C,?> group) {
        propertyGroups.put(name, group);
    }
    
    public void setDefaultPropertyGroup(PropertyGroup<C,?> group) {
        defaultPropertyGroup = group;
    }
    
    public Template<C> compile(String s) throws InvalidTemplateException {
        List<Expression<? super C>> expressions = new ArrayList<Expression<? super C>>();
        Scanner scanner = new Scanner(s);
        while (scanner.peek() != -1) {
            if (scanner.consume('%')) {
                PropertyGroup<C,?> group;
                String propertyName;
                String atom = scanner.parseAtom();
                if (scanner.consume('.')) {
                    group = propertyGroups.get(atom);
                    if (group == null) {
                        throw new InvalidTemplateException("Unknown property group '" + atom + "'");
                    }
                    propertyName = scanner.parseAtom();
                } else {
                    group = defaultPropertyGroup;
                    propertyName = atom;
                }
                String prefix = "";
                String suffix = "";
                String defaultValue = null;
                if (scanner.consume('?')) {
                    prefix = scanner.parseString("?@:%");
                    scanner.expect('@');
                    suffix = scanner.parseString("?@:%");
                    if (scanner.consume(':')) {
                        defaultValue = scanner.parseString("?@:%");
                    }
                }
                scanner.expect('%');
                PropertyExpression<C,?> expression = createPropertyExpression(group, propertyName, prefix, suffix, defaultValue);
                if (expression == null) {
                    throw new InvalidTemplateException("Unknown property '" + propertyName + "'");
                }
                expressions.add(expression);
            } else {
                expressions.add(new Text(scanner.parseString("%")));
            }
        }
        return new Template<C>(expressions);
    }
    
    private <CG> PropertyExpression<C,CG> createPropertyExpression(PropertyGroup<C,CG> group, String propertyName,
            String prefix, String suffix, String defaultValue) {
        Property<CG> property = group.getProperty(propertyName);
        return property == null ? null : new PropertyExpression<C,CG>(group, property, prefix, suffix, defaultValue);
    }
}

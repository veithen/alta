/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2024 Andreas Veithen
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

import java.util.Map;

final class PropertyExpression<C, GC> extends Expression<C> {
    private final PropertyGroup<C, GC> group;
    private final Property<GC> property;
    private final String prefix;
    private final String suffix;
    private final String defaultValue;

    public PropertyExpression(
            PropertyGroup<C, GC> group,
            Property<GC> property,
            String prefix,
            String suffix,
            String defaultValue) {
        this.group = group;
        this.property = property;
        this.prefix = prefix;
        this.suffix = suffix;
        this.defaultValue = defaultValue;
    }

    @Override
    boolean evaluate(C object, Map<Object, Object> contextMap, StringBuilder buffer)
            throws EvaluationException {
        GC groupContext;
        if (contextMap.containsKey(group)) {
            groupContext = group.getGroupContextClass().cast(contextMap.get(group));
        } else {
            groupContext = group.prepare(object);
            contextMap.put(group, groupContext);
        }
        String value = groupContext == null ? null : property.evaluate(groupContext);
        if (value != null) {
            buffer.append(prefix);
            buffer.append(value);
            buffer.append(suffix);
            return true;
        } else if (defaultValue != null) {
            buffer.append(defaultValue);
            return true;
        } else {
            return false;
        }
    }
}

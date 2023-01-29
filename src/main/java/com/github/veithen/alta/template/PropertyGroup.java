/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2023 Andreas Veithen
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

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a group of properties. Properties in a group are evaluated in a context that is specific
 * to the group and that is derived from the context object (that defines the context in which the
 * template is evaluated). This group context is created only once even if the group is used
 * multiple times in the template (see {@link #prepare(Object)}). It can therefore be used for
 * costly computations the results of which are used by all properties in the group.
 *
 * @param <C> the type of context in which the template is evaluated
 * @param <GC> the group context type
 */
public abstract class PropertyGroup<C, GC> {
    private final Class<GC> groupContextClass;
    private final Map<String, Property<GC>> properties = new HashMap<String, Property<GC>>();

    public PropertyGroup(Class<GC> groupContextClass) {
        this.groupContextClass = groupContextClass;
    }

    public final Class<GC> getGroupContextClass() {
        return groupContextClass;
    }

    public final void addProperty(String name, Property<GC> property) {
        properties.put(name, property);
    }

    public final Property<GC> getProperty(String name) {
        return properties.get(name);
    }

    /**
     * Create the group context for this property group and the given context object. The group
     * context is later passed to {@link Property#evaluate(Object)} when the property is evaluated.
     * During the evaluation of a template, this method is called exactly once for every property
     * group used in that template.
     *
     * @param object the context object
     * @return the group context or <code>null</code> if the property group is not supported for the
     *     given artifact
     * @throws EvaluationException
     */
    public abstract GC prepare(C object) throws EvaluationException;
}

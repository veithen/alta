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

abstract class Expression<C> {
    /**
     * Evaluate this expression.
     *
     * @param object the context object
     * @param contextMap a map that can be used to store shared data
     * @param buffer the buffer to append the result of the evaluation to
     * @return <code>true</code> if the expression was evaluated successfully; <code>false</code> if
     *     the expression is not supported for the given artifact
     * @throws EvaluationException
     */
    abstract boolean evaluate(C object, Map<Object, Object> contextMap, StringBuilder buffer)
            throws EvaluationException;
}

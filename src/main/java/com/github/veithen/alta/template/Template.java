/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2020 Andreas Veithen
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
import java.util.List;
import java.util.Map;

public final class Template<C> {
    private final List<Expression<? super C>> expressions;

    Template(List<Expression<? super C>> expressions) {
        this.expressions = expressions;
    }

    public String evaluate(C object) throws EvaluationException {
        Map<Object, Object> contextMap = new HashMap<Object, Object>();
        StringBuilder buffer = new StringBuilder();
        for (Expression<? super C> expression : expressions) {
            if (!expression.evaluate(object, contextMap, buffer)) {
                return null;
            }
        }
        return buffer.toString();
    }
}

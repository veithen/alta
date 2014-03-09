package com.github.veithen.alta.pattern;

import java.util.Map;

abstract class Expression<C> {
    /**
     * Evaluate this expression.
     * 
     * @param object
     *            the context object
     * @param contextMap
     *            a map that can be used to store shared data
     * @param buffer
     *            the buffer to append the result of the evaluation to
     * @return <code>true</code> if the expression was evaluated successfully; <code>false</code> if
     *         the expression is not supported for the given artifact
     */
    abstract boolean evaluate(C object, Map<Object,Object> contextMap, StringBuilder buffer);
}

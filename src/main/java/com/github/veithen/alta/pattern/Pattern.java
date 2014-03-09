package com.github.veithen.alta.pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Pattern<C> {
    private final List<Expression<? super C>> expressions;
    
    Pattern(List<Expression<? super C>> expressions) {
        this.expressions = expressions;
    }
    
    public String evaluate(C object) {
        Map<Object,Object> contextMap = new HashMap<Object,Object>();
        StringBuilder buffer = new StringBuilder();
        for (Expression<? super C> expression : expressions) {
            expression.evaluate(object, contextMap, buffer);
        }
        return buffer.toString();
    }
}

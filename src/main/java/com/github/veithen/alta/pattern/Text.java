package com.github.veithen.alta.pattern;

import java.util.Map;

final class Text extends Expression<Object> {
    private final String content;

    Text(String content) {
        this.content = content;
    }

    @Override
    boolean evaluate(Object object, Map<Object,Object> contextMap, StringBuilder buffer) {
        buffer.append(content);
        return true;
    }
}

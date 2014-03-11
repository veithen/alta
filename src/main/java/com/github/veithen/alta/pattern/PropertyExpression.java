package com.github.veithen.alta.pattern;

import java.util.Map;

final class PropertyExpression<C,GC> extends Expression<C> {
    private final PropertyGroup<C,GC> group;
    private final Property<GC> property;
    
    public PropertyExpression(PropertyGroup<C,GC> group, Property<GC> property) {
        this.group = group;
        this.property = property;
    }

    @Override
    boolean evaluate(C object, Map<Object,Object> contextMap, StringBuilder buffer) throws EvaluationException {
        GC groupContext = group.getGroupContextClass().cast(contextMap.get(group));
        if (groupContext == null) {
            groupContext = group.prepare(object);
            if (groupContext == null) {
                return false;
            }
            contextMap.put(group, groupContext);
        }
        buffer.append(property.evaluate(groupContext));
        return true;
    }
}

package com.github.veithen.alta.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PatternCompiler<C> {
    private Map<String,PropertyGroup<C,?>> propertyGroups = new HashMap<String,PropertyGroup<C,?>>();
    private PropertyGroup<C,?> defaultPropertyGroup;
    
    public void addPropertyGroup(String name, PropertyGroup<C,?> group) {
        propertyGroups.put(name, group);
    }
    
    public void setDefaultPropertyGroup(PropertyGroup<C,?> group) {
        defaultPropertyGroup = group;
    }
    
    public Pattern<C> compile(String s) throws InvalidPatternException {
        List<Expression<? super C>> expressions = new ArrayList<Expression<? super C>>();
        int pos = 0;
        while (pos < s.length()) {
            int idx1 = s.indexOf('%', pos);
            if (idx1 == -1) {
                expressions.add(new Text(s.substring(pos, s.length())));
                break;
            }
            int idx2 = s.indexOf('%', idx1+1);
            if (idx2 == -1) {
                throw new InvalidPatternException("Unmatched '%' at position " + idx1);
            }
            if (idx1 != pos) {
                expressions.add(new Text(s.substring(pos, idx1)));
            }
            String expressionString = s.substring(idx1+1, idx2);
            int dotIndex = expressionString.indexOf('.');
            PropertyGroup<C,?> group;
            String propertyName;
            if (dotIndex == -1) {
                group = defaultPropertyGroup;
                propertyName = expressionString;
            } else {
                String groupName = expressionString.substring(0, dotIndex);
                group = propertyGroups.get(groupName);
                if (group == null) {
                    throw new InvalidPatternException("Unknown property group '" + groupName + "' at position " + (idx1+1));
                }
                propertyName = expressionString.substring(dotIndex+1);
            }
            PropertyExpression<C,?> expression = createPropertyExpression(group, propertyName);
            if (expression == null) {
                throw new InvalidPatternException("Unknown property '" + propertyName + "' at position " + (idx1+1));
            }
            expressions.add(expression);
            pos = idx2+1;
        }
        return new Pattern<C>(expressions);
    }
    
    private <CG> PropertyExpression<C,CG> createPropertyExpression(PropertyGroup<C,CG> group, String propertyName) {
        Property<CG> property = group.getProperty(propertyName);
        return property == null ? null : new PropertyExpression<C,CG>(group, property);
    }
}

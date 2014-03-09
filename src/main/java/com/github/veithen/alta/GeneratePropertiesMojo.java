package com.github.veithen.alta;

import java.util.List;
import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

public final class GeneratePropertiesMojo extends AbstractGenerateMojo {
    @Parameter(required=true)
    private String propertyName;
    
    @Parameter(required=true)
    private String value;

    @Override
    protected final String getDestinationPattern() {
        return propertyName;
    }

    @Override
    protected final String getValuePattern() {
        return value;
    }

    @Override
    protected void process(Map<String, List<String>> result) {
        // TODO Auto-generated method stub
        
    }
    
}

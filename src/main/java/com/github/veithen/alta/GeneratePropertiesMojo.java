package com.github.veithen.alta;

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

@Mojo(name="generate-properties", defaultPhase=LifecyclePhase.INITIALIZE)
public final class GeneratePropertiesMojo extends AbstractGenerateMojo {
    @Parameter(required=true)
    private String propertyName;
    
    @Parameter(required=true)
    private String value;
    
    @Parameter
    private String separator;
    
    @Parameter(readonly=true, required=true, defaultValue="${project}")
    private MavenProject project;

    @Override
    protected final String getDestinationPattern() {
        return propertyName;
    }

    @Override
    protected final String getValuePattern() {
        return value;
    }

    @Override
    protected void process(Map<String,List<String>> result) throws MojoExecutionException, MojoFailureException {
        for (Map.Entry<String,List<String>> entry : result.entrySet()) {
            List<String> values = entry.getValue();
            String value;
            if (values.size() == 1) {
                value = values.get(0);
            } else {
                if (separator == null) {
                    throw new MojoExecutionException("No separator configured");
                }
                value = StringUtils.join(entry.getValue().iterator(), separator);
            }
            project.getProperties().setProperty(entry.getKey(), value);
        }
    }
}

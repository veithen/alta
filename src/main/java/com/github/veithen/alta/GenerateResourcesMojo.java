package com.github.veithen.alta;

import java.io.File;

import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name="generate-resources", defaultPhase=LifecyclePhase.GENERATE_RESOURCES)
public final class GenerateResourcesMojo extends AbstractGenerateResourcesMojo {
    /**
     * Output directory for generated resources.
     */
    @Parameter(required=true, defaultValue="${project.build.directory}/generated-resources/alta")
    private File outputDirectory;

    @Override
    protected File getOutputDirectory() {
        return outputDirectory;
    }
    
    @Override
    protected void addResource(MavenProject project, Resource resource) {
        project.addResource(resource);
    }
}

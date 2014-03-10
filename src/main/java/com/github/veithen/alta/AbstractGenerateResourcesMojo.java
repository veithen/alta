package com.github.veithen.alta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractGenerateResourcesMojo extends AbstractGenerateMojo {
    @Parameter(required=true)
    private String resourceName;
    
    @Parameter(required=true)
    private String output;
    
    @Override
    protected final String getDestinationPattern() {
        return resourceName;
    }

    @Override
    protected final String getValuePattern() {
        return output;
    }

    @Override
    protected void process(Map<String,List<String>> result) throws MojoExecutionException, MojoFailureException {
        File outputDirectory = getOutputDirectory();
        for (Map.Entry<String,List<String>> entry : result.entrySet()) {
            File outputFile = new File(outputDirectory, entry.getKey());
            outputFile.getParentFile().mkdirs();
            try {
                FileOutputStream fos = new FileOutputStream(outputFile);
                try {
                    // TODO: charset encoding
                    Writer out = new OutputStreamWriter(fos);
                    for (String value : entry.getValue()) {
                        out.write(value);
                        out.write("\n"); // TODO: make this configurable
                    }
                    out.flush();
                } finally {
                    fos.close();
                }
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to write resource " + entry.getKey(), ex);
            }
        }
        Resource resource = new Resource();
        resource.setDirectory(outputDirectory.getPath());
        addResource(project, resource);
    }

    protected abstract File getOutputDirectory();
    protected abstract void addResource(MavenProject project, Resource resource);
}

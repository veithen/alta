package com.github.veithen.alta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

public abstract class AbstractGenerateResourcesMojo extends AbstractGenerateMojo {
    private static final Map<String,String> paxExamCompatMap = new HashMap<String,String>();
    
    static {
        paxExamCompatMap.put("META-INF/links/org.ops4j.pax.exam.extender.service.link", "META-INF/links/org.ops4j.pax.extender.service.link");
        paxExamCompatMap.put("META-INF/links/osgi.cmpn.link", "META-INF/links/org.osgi.compendium.link");
        paxExamCompatMap.put("META-INF/links/org.ops4j.pax.logging.pax-logging-api.link", "META-INF/links/org.ops4j.pax.logging.api.link");
        paxExamCompatMap.put("META-INF/links/org.apache.geronimo.specs.geronimo-atinject_1.0_spec.link", "META-INF/links/org.apache.geronimo.specs.atinject.link");
    }
    
    @Override
    protected void process(Map<String,List<String>> result) throws MojoExecutionException, MojoFailureException {
        File outputDirectory = getOutputDirectory();
        for (Map.Entry<String,List<String>> entry : result.entrySet()) {
            String resource = entry.getKey();
            String replacementResource = paxExamCompatMap.get(resource);
            if (replacementResource != null) {
                getLog().info("Using " + replacementResource + " instead of " + resource + " for compatibility with Pax Exam");
                resource = replacementResource;
            }
            File outputFile = new File(outputDirectory, resource);
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
                throw new MojoExecutionException("Failed to write resource " + resource, ex);
            }
        }
        Resource resource = new Resource();
        resource.setDirectory(outputDirectory.getPath());
        addResource(project, resource);
    }

    protected abstract File getOutputDirectory();
    protected abstract void addResource(MavenProject project, Resource resource);
}

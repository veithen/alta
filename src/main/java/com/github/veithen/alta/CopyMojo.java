/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2020 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.alta;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.FileUtils;

import com.github.veithen.alta.template.Template;

@Mojo(name="copy", requiresDependencyResolution=ResolutionScope.TEST, threadSafe=true)
public class CopyMojo extends AbstractProcessMojo {
    /**
     * Directory to copy artifacts to.
     */
    @Parameter(required=true)
    private File outputDirectory;

    /**
     * The output file name template.
     */
    @Parameter(required=true)
    private String name;

    @Override
    protected void process(List<Artifact> artifacts) throws MojoExecutionException, MojoFailureException {
        Log log = getLog();

        Template<Context> nameTemplate = compileTemplate(name, "name");
        for (Artifact artifact : artifacts) {
            if (log.isDebugEnabled()) {
                log.debug("Processing artifact " + artifact.getId());
            }
            String name = evaluateTemplate(nameTemplate, artifact);
            if (name == null) {
                log.debug("Not copying artifact");
                continue;
            }
            File outputFile = new File(outputDirectory, name);
            if (log.isDebugEnabled()) {
                log.debug("Copying artifact to " + outputFile);
            }
            try {
                FileUtils.copyFile(artifact.getFile(), outputFile);
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to copy file", ex);
            }
        }
    }
}

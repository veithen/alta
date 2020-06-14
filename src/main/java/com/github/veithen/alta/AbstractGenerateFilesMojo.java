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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractGenerateFilesMojo extends AbstractGenerateMojo {
    /**
     * The charset encoding to use for the generated files.
     */
    @Parameter(required=true, defaultValue="UTF-8")
    private String encoding;

    @Override
    protected final void process(Map<String,String> result) throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        File outputDirectory = getOutputDirectory();
        for (Map.Entry<String,String> entry : result.entrySet()) {
            String resource = entry.getKey();
            File outputFile = new File(outputDirectory, resource);
            File parentDir = outputFile.getParentFile();
            if (!parentDir.exists()) {
                if (log.isDebugEnabled()) {
                    log.debug("Creating directory " + parentDir);
                }
                if (!parentDir.mkdirs()) {
                    throw new MojoExecutionException("Unable to create directory " + parentDir);
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(outputFile);
                try {
                    Writer out = new OutputStreamWriter(fos, encoding);
                    out.write(entry.getValue());
                    out.flush();
                } finally {
                    fos.close();
                }
            } catch (IOException ex) {
                throw new MojoExecutionException("Failed to write file " + outputFile, ex);
            }
        }
        postProcess(outputDirectory);
    }

    protected abstract File getOutputDirectory();
    protected abstract void postProcess(File outputDirectory);
}

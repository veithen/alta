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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.veithen.alta.template.Template;

public abstract class AbstractGenerateMojo extends AbstractProcessMojo {
    /**
     * The destination name template.
     */
    @Parameter(required=true)
    private String name;
    
    /**
     * The template of the value to generate.
     */
    @Parameter(required=true)
    private String value;
    
    /**
     * The separator that should be used to join values when multiple artifacts map to the same
     * name. If no separator is configured, then duplicate names will result in an error. To
     * separate values by newlines, use the <code>line.separator</code> property.
     */
    @Parameter
    private String separator;

    @Override
    protected final void process(List<Artifact> artifacts) throws MojoExecutionException, MojoFailureException {
        Log log = getLog();

        Template<Context> nameTemplate = compileTemplate(name, "name");
        Template<Context> valueTemplate = compileTemplate(value, "value");
        Map<String,String> result = new HashMap<String,String>();
        for (Artifact artifact : artifacts) {
            if (log.isDebugEnabled()) {
                log.debug("Processing artifact " + artifact.getId());
            }
            String name = evaluateTemplate(nameTemplate, artifact);
            if (log.isDebugEnabled()) {
                log.debug("name = " + name);
            }
            if (name == null) {
                continue;
            }
            String value = evaluateTemplate(valueTemplate, artifact);
            if (log.isDebugEnabled()) {
                log.debug("value = " + value);
            }
            if (value == null) {
                continue;
            }
            String currentValue = result.get(name);
            if (currentValue == null) {
                currentValue = value;
            } else if (separator == null) {
                throw new MojoExecutionException("No separator configured");
            } else {
                currentValue = currentValue + separator + value;
            }
            result.put(name, currentValue);
        }
        process(result);
    }
    
    protected abstract void process(Map<String,String> result) throws MojoExecutionException, MojoFailureException;
}

/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 The Alta Maven Plugin Authors.
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

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

@Mojo(name="generate-properties", defaultPhase=LifecyclePhase.INITIALIZE)
public final class GeneratePropertiesMojo extends AbstractGenerateMojo {
    @Parameter
    private String separator;
    
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

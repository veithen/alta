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

import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
        name = "generate-properties",
        requiresDependencyResolution = ResolutionScope.TEST,
        defaultPhase = LifecyclePhase.INITIALIZE,
        threadSafe = true)
public final class GeneratePropertiesMojo extends AbstractGenerateMojo {
    @Override
    protected void process(Map<String, String> result)
            throws MojoExecutionException, MojoFailureException {
        project.getProperties().putAll(result);
    }
}

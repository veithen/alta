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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Repository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.github.veithen.alta.template.EvaluationException;
import com.github.veithen.alta.template.InvalidTemplateException;
import com.github.veithen.alta.template.PropertyGroup;
import com.github.veithen.alta.template.Template;
import com.github.veithen.alta.template.TemplateCompiler;
import com.github.veithen.maven.shared.artifactset.ArtifactSet;
import com.github.veithen.maven.shared.artifactset.ArtifactSetResolver;
import com.github.veithen.maven.shared.artifactset.ArtifactSetResolverException;

public abstract class AbstractProcessMojo extends AbstractMojo {
    private static final TemplateCompiler<Context> templateCompiler;
    
    static {
        templateCompiler = new TemplateCompiler<Context>();
        PropertyGroup<Context,Artifact> artifactGroup = new PropertyGroup<Context,Artifact>(Artifact.class) {
            @Override
            public Artifact prepare(Context context) throws EvaluationException {
                return context.getArtifact();
            }
        };
        artifactGroup.addProperty("artifactId", Artifact::getArtifactId);
        artifactGroup.addProperty("groupId", Artifact::getGroupId);
        artifactGroup.addProperty("version", Artifact::getVersion);
        artifactGroup.addProperty("classifier", Artifact::getClassifier);
        artifactGroup.addProperty("type", Artifact::getType);
        artifactGroup.addProperty("file", artifact -> getArtifactFile(artifact).getPath());
        artifactGroup.addProperty("url", artifact -> {
            try {
                return getArtifactFile(artifact).toURI().toURL().toString();
            } catch (MalformedURLException ex) {
                throw new EvaluationException("Unexpected exception", ex);
            }
        });
        templateCompiler.setDefaultPropertyGroup(artifactGroup);
        PropertyGroup<Context,Bundle> bundleGroup = new PropertyGroup<Context,Bundle>(Bundle.class) {
            @Override
            public Bundle prepare(Context context) throws EvaluationException {
                return extractBundleMetadata(context.getArtifact());
            }
        };
        bundleGroup.addProperty("symbolicName", Bundle::getSymbolicName);
        templateCompiler.addPropertyGroup("bundle", bundleGroup);
    }
    

    @Parameter(required=true)
    private ArtifactSet artifactSet;

    @Parameter
    private Repository[] repositories;

    @Parameter(property="project", readonly=true, required=true)
    protected MavenProject project;
    
    @Parameter(property="session", readonly=true, required=true)
    private MavenSession session;
    
    @Parameter(defaultValue="false")
    private boolean skip;

    @Component
    private ArtifactSetResolver artifactSetResolver;

    public final void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();

        if (skip) {
            log.info("Skipping plugin execution");
        }

        List<Artifact> artifacts;
        try {
            artifacts = artifactSetResolver.resolveArtifactSet(project, session, artifactSet, repositories);
        } catch (ArtifactSetResolverException ex) {
            throw new MojoExecutionException("Failed to resolve artifact set", ex);
        }
        process(artifacts);
    }

    protected abstract void process(List<Artifact> artifacts) throws MojoExecutionException, MojoFailureException;

    static File getArtifactFile(Artifact artifact) throws EvaluationException {
        File file = artifact.getFile();
        if (file.isFile()) {
            return file;
        } else {
            throw new EvaluationException("Artifact has not been packaged yet; it is part of the reactor, but the package phase has not been executed.");
        }
    }
    
    static Bundle extractBundleMetadata(Artifact artifact) throws EvaluationException {
        File file = getArtifactFile(artifact);
        try {
            Manifest manifest = null;
            InputStream in = new FileInputStream(file);
            try {
                ZipInputStream zip = new ZipInputStream(in);
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        manifest = new Manifest(zip);
                        break;
                    }
                }
            } finally {
                in.close();
            }
            if (manifest != null) {
                String symbolicName = manifest.getMainAttributes().getValue("Bundle-SymbolicName");
                if (symbolicName != null) {
                    int idx = symbolicName.indexOf(';');
                    if (idx != -1) {
                        symbolicName = symbolicName.substring(0, idx);
                    }
                    return new Bundle(symbolicName.trim());
                }
            }
            return null;
        } catch (IOException ex) {
            throw new EvaluationException("Failed to read " + file, ex);
        }
    }

    protected static Template<Context> compileTemplate(String template, String name) throws MojoExecutionException {
        try {
            return templateCompiler.compile(template);
        } catch (InvalidTemplateException ex) {
            throw new MojoExecutionException("Invalid " + name + " template", ex);
        }
    }

    protected static String evaluateTemplate(Template<Context> template, Artifact artifact) throws MojoExecutionException {
        try {
            return template.evaluate(new Context(artifact));
        } catch (EvaluationException ex) {
            throw new MojoExecutionException("Failed to process artifact " + artifact.getId() + ": " + ex.getMessage(), ex);
        }
    }
}

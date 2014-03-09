package com.github.veithen.alta;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.veithen.alta.pattern.InvalidPatternException;
import com.github.veithen.alta.pattern.Pattern;
import com.github.veithen.alta.pattern.PatternCompiler;
import com.github.veithen.alta.pattern.Property;
import com.github.veithen.alta.pattern.PropertyGroup;

public abstract class AbstractGenerateMojo extends AbstractMojo {
    private static final PatternCompiler<Artifact> patternCompiler;
    
    static {
        patternCompiler = new PatternCompiler<Artifact>();
        PropertyGroup<Artifact,Artifact> artifactGroup = new PropertyGroup<Artifact,Artifact>(Artifact.class) {
            @Override
            public Artifact prepare(Artifact object) {
                return object;
            }
        };
        artifactGroup.addProperty("artifactId", new Property<Artifact>() {
            public String evaluate(Artifact artifact) {
                return artifact.getArtifactId();
            }
        });
        artifactGroup.addProperty("groupId", new Property<Artifact>() {
            public String evaluate(Artifact artifact) {
                return artifact.getGroupId();
            }
        });
        artifactGroup.addProperty("version", new Property<Artifact>() {
            public String evaluate(Artifact artifact) {
                return artifact.getVersion();
            }
        });
        artifactGroup.addProperty("url", new Property<Artifact>() {
            public String evaluate(Artifact artifact) {
                try {
                    return artifact.getFile().toURI().toURL().toString();
                } catch (MalformedURLException ex) {
                    throw new Error("Unexpected exception", ex);
                }
            }
        });
        patternCompiler.setDefaultPropertyGroup(artifactGroup);
        // TODO: bundle support
    }
    
    @Parameter
    private ArtifactItem[] artifacts;
    
    @Component
    private ArtifactFactory factory;
    
    @Component
    private ArtifactResolver resolver;
    
    @Parameter(readonly=true, required=true, defaultValue="${project.remoteArtifactRepositories}")
    private List<ArtifactRepository> remoteRepos;
    
    @Parameter(readonly=true, required=true, defaultValue="${localRepository}")
    private ArtifactRepository localRepository;
    
    public final void execute() throws MojoExecutionException, MojoFailureException {
        Pattern<Artifact> destinationPattern;
        try {
            destinationPattern = patternCompiler.compile(getDestinationPattern());
        } catch (InvalidPatternException ex) {
            throw new MojoExecutionException("Invalid destination pattern", ex);
        }
        Pattern<Artifact> valuePattern;
        try {
            valuePattern = patternCompiler.compile(getValuePattern());
        } catch (InvalidPatternException ex) {
            throw new MojoExecutionException("Invalid value pattern", ex);
        }
        List<Artifact> resolvedArtifacts = new ArrayList<Artifact>();
        if (artifacts != null) {
            for (ArtifactItem artifactItem : artifacts) {
                Artifact artifact = factory.createDependencyArtifact(artifactItem.getGroupId(), artifactItem.getArtifactId(),
                        VersionRange.createFromVersion(artifactItem.getVersion()), artifactItem.getType(),
                        artifactItem.getClassifier(), Artifact.SCOPE_COMPILE);
                try {
                    resolver.resolve(artifact, remoteRepos, localRepository);
                } catch (ArtifactResolutionException ex) {
                    throw new MojoExecutionException("Unable to resolve artifact", ex);
                } catch (ArtifactNotFoundException ex) {
                    throw new MojoExecutionException("Artifact not found", ex);
                }
                resolvedArtifacts.add(artifact);
            }
        }
        Map<String,List<String>> result = new HashMap<String,List<String>>();
        for (Artifact artifact : resolvedArtifacts) {
            String destination = destinationPattern.evaluate(artifact);
            List<String> values = result.get(destination);
            if (values == null) {
                values = new ArrayList<String>();
                result.put(destination, values);
            }
            values.add(valuePattern.evaluate(artifact));
        }
        process(result);
    }
    
    protected abstract String getDestinationPattern();
    protected abstract String getValuePattern();
    protected abstract void process(Map<String,List<String>> result) throws MojoExecutionException, MojoFailureException;
}

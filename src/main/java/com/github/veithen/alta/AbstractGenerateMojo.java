package com.github.veithen.alta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionNode;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Repository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.apache.maven.repository.RepositorySystem;

import com.github.veithen.alta.pattern.EvaluationException;
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
            public Artifact prepare(Artifact object) throws EvaluationException {
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
        artifactGroup.addProperty("file", new Property<Artifact>() {
            public String evaluate(Artifact artifact) {
                return artifact.getFile().getPath();
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
        PropertyGroup<Artifact,Bundle> bundleGroup = new PropertyGroup<Artifact,Bundle>(Bundle.class) {
            @Override
            public Bundle prepare(Artifact artifact) throws EvaluationException {
                try {
                    return extractBundleMetadata(artifact.getFile());
                } catch (IOException ex) {
                    throw new EvaluationException("Failed to read " + artifact.getFile(), ex);
                }
            }
        };
        bundleGroup.addProperty("symbolicName", new Property<Bundle>() {
            public String evaluate(Bundle bundle) {
                return bundle.getSymbolicName();
            }
        });
        patternCompiler.addPropertyGroup("bundle", bundleGroup);
        PropertyGroup<Artifact,PaxExamInfo> paxExamGroup = new PaxExamGroup();
        paxExamGroup.addProperty("linkName", new Property<PaxExamInfo>() {
            public String evaluate(PaxExamInfo paxExamInfo) {
                return paxExamInfo.getLinkName();
            }
        });
        patternCompiler.addPropertyGroup("paxexam", paxExamGroup);
    }
    
    /**
     * The destination name pattern, i.e. the name of the resource or Maven property.
     */
    @Parameter(required=true)
    private String name;
    
    /**
     * An alternate destination name pattern. This is used if the pattern specified by the
     * <tt>name</tt> parameter is not resolvable (because it contains a reference to a property
     * that is not supported for the given artifact).
     */
    @Parameter
    private String altName;
    
    /**
     * The pattern of the value to generate.
     */
    @Parameter(required=true)
    private String value;
    
    @Parameter
    private DependencySet dependencySet;
    
    @Parameter
    private ArtifactItem[] artifacts;
    
    @Parameter
    private Repository[] repositories;
    
    @Component
    private RepositorySystem repositorySystem;
    
    @Component
    private ArtifactFactory factory;
    
    @Component
    private ArtifactCollector artifactCollector;
    
    @Component
    private ArtifactMetadataSource artifactMetadataSource;
    
    @Component
    private ArtifactResolver resolver;

    @Parameter(readonly=true, required=true, defaultValue="${project}")
    protected MavenProject project;
    
    @Parameter(readonly=true, required=true, defaultValue="${localRepository}")
    private ArtifactRepository localRepository;
    
    public final void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        Pattern<Artifact> namePattern;
        try {
            namePattern = patternCompiler.compile(name);
        } catch (InvalidPatternException ex) {
            throw new MojoExecutionException("Invalid destination name pattern", ex);
        }
        Pattern<Artifact> altNamePattern;
        if (altName == null) {
            altNamePattern = null;
        } else {
            try {
                altNamePattern = patternCompiler.compile(altName);
            } catch (InvalidPatternException ex) {
                throw new MojoExecutionException("Invalid altName pattern", ex);
            }
        }
        Pattern<Artifact> valuePattern;
        try {
            valuePattern = patternCompiler.compile(value);
        } catch (InvalidPatternException ex) {
            throw new MojoExecutionException("Invalid value pattern", ex);
        }
        List<Artifact> resolvedArtifacts = new ArrayList<Artifact>();
        if (dependencySet != null) {
            if (log.isDebugEnabled()) {
                log.debug("Resolving project dependencies in scope " + dependencySet.getScope());
            }
            ArtifactFilter filter = new ScopeArtifactFilter(dependencySet.getScope());
            Set<Artifact> artifacts;
            try {
                artifacts = MavenMetadataSource.createArtifacts(factory, project.getDependencies(), null, filter, project);
            } catch (InvalidDependencyVersionException ex) {
                throw new MojoExecutionException("Failed to collect project dpendencies", ex);
            }
            // Note: dependencies are always resolved from the repositories declared in the POM, never
            // from repositories declared in the plugin configuration
            try {
                resolvedArtifacts.addAll(resolver.resolveTransitively(artifacts, project.getArtifact(), project.getRemoteArtifactRepositories(), localRepository, artifactMetadataSource).getArtifacts());
            } catch (ArtifactResolutionException ex) {
                throw new MojoExecutionException("Unable to resolve artifact", ex);
            } catch (ArtifactNotFoundException ex) {
                throw new MojoExecutionException("Artifact not found", ex);
            }
        }
        if (artifacts != null && artifacts.length != 0) {
            List<ArtifactRepository> pomRepositories = project.getRemoteArtifactRepositories();
            List<ArtifactRepository> effectiveRepositories;
            if (repositories != null && repositories.length > 0) {
                effectiveRepositories = new ArrayList<ArtifactRepository>(pomRepositories.size() + repositories.length);
                effectiveRepositories.addAll(pomRepositories);
                for (Repository repository : repositories) {
                    try {
                        effectiveRepositories.add(repositorySystem.buildArtifactRepository(repository));
                    } catch (InvalidRepositoryException ex) {
                        throw new MojoExecutionException("Invalid repository", ex);
                    }
                }
            } else {
                effectiveRepositories = pomRepositories;
            }
            for (ArtifactItem artifactItem : artifacts) {
                VersionRange version;
                try {
                    version = VersionRange.createFromVersionSpec(artifactItem.getVersion());
                } catch (InvalidVersionSpecificationException ex) {
                    throw new MojoExecutionException("Invalid version specified for artifact " + artifactItem.getGroupId() + ":" + artifactItem.getArtifactId(), ex);
                }
                Artifact artifact = factory.createDependencyArtifact(artifactItem.getGroupId(), artifactItem.getArtifactId(),
                        version, artifactItem.getType(), artifactItem.getClassifier(), Artifact.SCOPE_COMPILE);
                try {
                    // Find an appropriate version in the specified version range
                    ArtifactResolutionResult artifactResolutionResult = artifactCollector.collect(Collections.singleton(artifact), project.getArtifact(), localRepository, effectiveRepositories, artifactMetadataSource, null, Collections.EMPTY_LIST);
                    artifact = ((ResolutionNode)artifactResolutionResult.getArtifactResolutionNodes().iterator().next()).getArtifact();
                    
                    // Download the artifact
                    resolver.resolve(artifact, effectiveRepositories, localRepository);
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
            if (log.isDebugEnabled()) {
                log.debug("Processing artifact " + artifact.getId());
            }
            try {
                String name = namePattern.evaluate(artifact);
                if (log.isDebugEnabled()) {
                    log.debug("name = " + name);
                }
                if (name == null && altNamePattern != null) {
                    log.debug("Using altName");
                    name = altNamePattern.evaluate(artifact);
                    if (log.isDebugEnabled()) {
                        log.debug("name = " + name);
                    }
                }
                if (name == null) {
                    continue;
                }
                String value = valuePattern.evaluate(artifact);
                if (log.isDebugEnabled()) {
                    log.debug("value = " + value);
                }
                if (value == null) {
                    continue;
                }
                List<String> values = result.get(name);
                if (values == null) {
                    values = new ArrayList<String>();
                    result.put(name, values);
                }
                values.add(value);
            } catch (EvaluationException ex) {
                throw new MojoExecutionException("Failed to process artifact " + artifact.getId());
            }
        }
        process(result);
    }
    
    static Bundle extractBundleMetadata(File file) throws IOException {
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
    }
    
    protected abstract void process(Map<String,List<String>> result) throws MojoExecutionException, MojoFailureException;
}

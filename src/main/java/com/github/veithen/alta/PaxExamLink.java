package com.github.veithen.alta;

import org.apache.maven.artifact.Artifact;

final class PaxExamLink {
    private final String linkName;
    private final Artifact artifact;
    
    PaxExamLink(String linkName, Artifact artifact) {
        super();
        this.linkName = linkName;
        this.artifact = artifact;
    }

    String getLinkName() {
        return linkName;
    }

    Artifact getArtifact() {
        return artifact;
    }
}

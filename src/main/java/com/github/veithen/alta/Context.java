package com.github.veithen.alta;

import java.util.List;

import org.apache.maven.artifact.Artifact;

final class Context {
    private final Artifact artifact;
    private final List<PaxExamLink> paxExamLinks;

    Context(Artifact artifact, List<PaxExamLink> paxExamLinks) {
        this.artifact = artifact;
        this.paxExamLinks = paxExamLinks;
    }

    Artifact getArtifact() {
        return artifact;
    }

    List<PaxExamLink> getPaxExamLinks() {
        return paxExamLinks;
    }
}

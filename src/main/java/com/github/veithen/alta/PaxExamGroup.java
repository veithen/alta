package com.github.veithen.alta;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;

import com.github.veithen.alta.pattern.EvaluationException;
import com.github.veithen.alta.pattern.PropertyGroup;

final class PaxExamGroup extends PropertyGroup<Artifact,PaxExamInfo> {
    private static Properties links;
    
    static {
        links = new Properties();
        try {
            InputStream in = PaxExamGroup.class.getResourceAsStream("paxexam-links.properties");
            try {
                links.load(in);
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new Error("Unexpected exception", ex);
        }
    }
    
    PaxExamGroup() {
        super(PaxExamInfo.class);
    }

    @Override
    public PaxExamInfo prepare(Artifact artifact) throws EvaluationException {
        String linkName = links.getProperty(artifact.getGroupId() + ":" + artifact.getArtifactId());
        return linkName == null ? null : new PaxExamInfo(linkName);
    }
}

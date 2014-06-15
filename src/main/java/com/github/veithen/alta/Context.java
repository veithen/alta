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

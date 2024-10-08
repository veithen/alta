<!--
  #%L
  Alta Maven Plugin
  %%
  Copyright (C) 2014 - 2024 Andreas Veithen
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  #L%
  -->

# Using Pax Exam in Maven builds

The [recommended approach](https://ops4j1.jira.com/wiki/display/paxexam/Pax+Exam+-+Tutorial+1)
to run OSGi unit tests with Pax Exam in a Maven build relies on the `pax-url-aether` library.
This library allows to resolve Maven artifacts and to provision them as bundles to the OSGi
runtime started by Pax Exam. Assuming that the bundles to be provisioned are declared as
Maven dependencies in the project, a typical Pax Exam configuration would look as follows
(Note that `asInProject()` relies on the execution of the
`org.apache.servicemix.tooling:depends-maven-plugin` during the build):

    @Configuration
    public static Option[] configuration() {
        return options(
            mavenBundle().groupId("org.apache.felix")
                         .artifactId("org.apache.felix.configadmin")
                         .version(asInProject()),
            junitBundles());
    }

While at first glance this looks straightforward and natural, there are a couple of issues with this approach.
The problem is that `pax-url-aether` creates its own Maven session to resolve artifacts, thereby bypassing
the underlying Maven build. This means that the resolution performed by Pax Exam doesn't necessarily use the
same configuration as the Maven build. There are several known circumstances where this causes problems:

1.  Repositories configured in the POM. Some have [argued](https://groups.google.com/forum/#!msg/ops4j/kRxAXidbt7A/w0i6tM1Mn9MJ)
    that declaring repositories in POMs is discouraged. That argument is correct for repositories containing release artifacts,
    but not for snapshot repositories: all release dependencies should indeed be available from the central repository, but
    dependencies on snapshot versions from upstream projects necessarily require configuration of additional repositories.
    The right place to configure these repositories is in the POM, not in `settings.xml`.

2.  The location of the local Maven repository (normally specified in `settings.xml`) can be overridden using
    the `maven.repo.local` system property. However, Pax Exam only looks at `settings.xml`.
    While overriding the local Maven repository on the command line is rarely done when running Maven manually,
    it is quite common for builds executed by a CI tool. E.g. Jenkins has a "Use private Maven repository" option that does
    exactly that (with a local repository in the Jenkins workspace). This problem is described in
    [PAXEXAM-543](https://ops4j1.jira.com/browse/PAXEXAM-543).

3.  Offline mode. This mode is enabled using the `-o` switch on the `mvn` command, but Pax Exam has no
    way to detect this and will continue trying to access remote Maven repositories.

Probably this list is not exhaustive and there are other POM settings that will cause similar problems.

Another problem is that when building a multi-module project with `mvn clean verify` (instead of `mvn clean install`),
instead of resolving bundles from the reactor, `pax-url-aether` will either use potentially outdated artifacts from
the local (or a remote) repository or fail because it can't find them. As described
[here](https://groups.google.com/d/topic/ops4j/EXtrOLSAWG8/discussion), this causes `maven-release-plugin` to fail during
release preparation, unless the `preparationGoals` parameter is overridden with `clean install`.

Actually the whole approach of having Pax Exam resolve Maven dependencies on its own is questionable.
This is certainly a very useful feature when used outside of a Maven build (e.g. to provision bundles directly
from a Maven repository to a stand-alone OSGi container), but in a Maven build, it should be Maven's responsibility
to download artifacts, and Pax Exam's role should be limited to provisioning them to the embedded OSGi container.

The [`link:` protocol](https://ops4j1.jira.com/wiki/display/paxurl/Link+Protocol) (together with the
[`classpath:` protocol](https://ops4j1.jira.com/wiki/display/paxurl/Classpath+Protocol)) supported by Pax URL
comes to our rescue to solve this problem. The idea is to let Maven resolve the artifacts and to generate link
files that contain `file:` URLs to the bundles in the local Maven repository. They can then be deployed
using `url` options:

<!-- MACRO{snippet|id=configuration|file=src/it/pax-exam/src/test/java/OsgiTest.java} -->

These files can easily be generated using [alta:generate-test-resources](../generate-test-resources-mojo.html) with
the following configuration:

<!-- MACRO{snippet|id=plugin|file=src/it/pax-exam/pom.xml} -->

This configuration assumes that the bundle to be deployed is declared as a dependency in scope
compile or test in the POM:

<!-- MACRO{snippet|id=dependency|file=src/it/pax-exam/pom.xml} -->

Note that this approach completely eliminates the usage of the `mvn:` protocal only if you
use `pax-exam-link-assembly` instead of `pax-exam-link-mvn`.
This means that the other dependencies of your project should look something like this:

<!-- MACRO{snippet|id=pax-exam-dependencies|file=src/it/pax-exam/pom.xml} -->

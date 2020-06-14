<!--
  #%L
  Alta Maven Plugin
  %%
  Copyright (C) 2014 - 2020 Andreas Veithen
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

## Single module Pax Exam configuration

The Pax Exam setup shown in the [previous example](./pax-exam.html) works well for
multi-module Maven projects with a dedicated Maven module for OSGi integration tests. However it is
often desirable to execute the tests directly in the Maven module that builds the bundle being tested.
That's especially true for small projects that could be implemented as single-module Maven projects.

It is important to remember that OSGi tests only work with fully packaged artifacts. To execute them
in the same module, we need to configure them as integration tests executed by
[`maven-failsafe-plugin`](http://maven.apache.org/surefire/maven-failsafe-plugin/) (rather than unit
tests executed by [`maven-surefire-plugin`](http://maven.apache.org/surefire/maven-surefire-plugin/).
In addition to that, we need to generate a link file for the bundle built by the current module.
This can be achieved by setting the `useProjectArtifact` option to `true` in the
[`dependencySet`](../artifact-sets.html#class_dependencySet), but this won't work with the
`generate-test-resources` goal because it would be executed before the `package` phase.
Instead, we use an execution of the `generate-files` goal bound to the `pre-integration-test` phase:

<!-- MACRO{snippet|id=alta-maven-plugin|file=src/it/pax-exam-single-module/pom.xml} -->

The directory containing the generated link files can then be included in the classpath used by
`maven-failsafe-plugin` by setting the
[`additionalClasspathElements`](http://maven.apache.org/surefire/maven-failsafe-plugin/integration-test-mojo.html#additionalClasspathElements)
parameter:

<!-- MACRO{snippet|id=maven-failsafe-plugin|file=src/it/pax-exam-single-module/pom.xml} -->

This makes it possible to reference these link files (including the link file for the bundle built
by the current project) using `link:classpath:` URLs, in the same way as in the multi-module
setup:

<!-- MACRO{snippet|id=configuration|file=src/it/pax-exam-single-module/src/test/java/test/ITCase.java} -->

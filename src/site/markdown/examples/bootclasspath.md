<!--
  #%L
  Alta Maven Plugin
  %%
  Copyright (C) 2014 - 2018 Andreas Veithen
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

## Configuring endorsed libraries in unit tests (Java <= 8)

In some cases, you need to run your unit tests with certain APIs at a different
version level than what is provided by the JRE, either because you want to ensure
that your code is compatible with older versions of these APIs or because
the versions of the APIs included in the JRE are not the most recent ones, but
your code expects the latest versions.

In a Maven build this is typically achieved by using
[maven-dependency-plugin](http://maven.apache.org/plugins/maven-dependency-plugin/) to
copy these libraries to a folder under `target/` and configuring
[maven-surefire-plugin](http://maven.apache.org/surefire/maven-surefire-plugin/)
to add `-Djava.endorsed.dirs=...` (with the directory containing the JARs) or
`-Xbootclasspath/p:...` (with the list of individual JARs) to the command line
of the JVM that executes the tests.

Copying the JARs to the project build directory is an extra step that should not be
necessary, considering that the JARs are already in the local Maven repository.
To avoid this extra step, one needs to compute the paths to the artifacts in the
local repository and pass the list of these paths to the `-Xbootclasspath/p:...`
JVM argument.

This can easily be achieved with the help of the
[alta:generate-properties](../generate-properties-mojo.html) goal:

<!-- MACRO{snippet|id=plugins|file=src/it/bootclasspath/pom.xml} -->

**Note:** This configuration uses `surefire.bootclasspath` as property name. You should
avoid using `bootclasspath` here because that property is used as a default value
for the [`bootclasspath`](https://maven.apache.org/plugins/maven-javadoc-plugin/jar-mojo.html#bootclasspath)
parameter by `maven-javadoc-plugin`.

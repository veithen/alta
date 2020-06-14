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

## Configuring Surefire Plugin with AspectJ load-time weaving

Executing unit tests with [AspectJ load-time weaving](https://eclipse.org/aspectj/doc/released/devguide/ltw.html)
enabled requires configuring `maven-surefire-plugin` to add a `-javaagent` option to the
JVM arguments that sets up the `aspectjweaver` library as a JVM agent. This use case is similar to
[configuring endorsed libraries](./bootclasspath.html), and the necessary configuration looks like this:

<!-- MACRO{snippet|id=plugins|file=src/it/agent/pom.xml} -->

The plugin configuration shown above doesn't specify the version of the `org.aspectj:aspectjweaver` artifact.
In this case, the plugin will determine the version from the dependencies or the dependency management configuration
of the project (This feature is supported starting with version 0.3 of the plugin). For AspectJ you would typically
add the following configuration to the project or its parent POM, in order to select compatible versions
of the weaver and the AspectJ runtime library (the latter being required as a test dependency of the project):

<!-- MACRO{snippet|id=dependencyManagement|file=src/it/agent/pom.xml} -->

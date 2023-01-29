<!--
  #%L
  Alta Maven Plugin
  %%
  Copyright (C) 2014 - 2023 Andreas Veithen
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

## Introduction

### Overview

This plugin takes as input a set of Maven artifacts specified as a subset of
the project dependencies or as an explicit list of artifacts and produces
a set of outputs with information about these artifacts.
Different types of output are supported. Conceptually they all form a map like
structure as described by the following table:

| Type of output         | Goal                                                        | Name (key)    | Value          |
| ---------------------- | ----------------------------------------------------------- | ------------- | -------------- |
| Maven project property | [alta:generate-properties](./generate-properties-mojo.html) | Property name | Property value |
| File                   | [alta:generate-files](./generate-files-mojo.html)           | File name     | File content   |
| Resource               | [alta:generate-resources](./generate-resources-mojo.html)<br/>[alta:generate-test-resources](./generate-test-resources-mojo.html) | Resource name  | File content |

Both the key (property or file name) and the value (property value or file content) are specified as configurable templates that are evaluated
for each artifact. The template language uses a simple `%property%` syntax.
For example, `%groupId%:%artifactId%:%version%` would evaluate to the Maven coordinates of the artifact.
This syntax was chosen to avoid conflicts with Maven property substitution (`${property}`) and the syntax
used by [maven-invoker-plugin](http://maven.apache.org/plugins/maven-invoker-plugin/) (`@property@`).
The complete list of supported properties can be found [here](./properties.html).

### Example

All this sounds fairly abstract. A concrete example will help to better understand how the plugin works.
Assume that the plugin is used on a project with the following dependencies:

<!-- MACRO{snippet|id=dependencies|file=src/it/intro-sample/pom.xml} -->

Further assume that the name and value templates are configured as follows:

| ----- | ------------------------------------------ |
| Name  | `META-INF/versions/%groupId%/%artifactId%` |
| Value | `%version%`                                |

With this configuration the [alta:generate-resources](./generate-resources-mojo.html) goal would
produce the following four resources corresponding to the transitive dependencies of the project:

    META-INF/versions/commons-codec/commons-codec
    META-INF/versions/commons-logging/commons-logging
    META-INF/versions/org.apache.httpcomponents/httpclient
    META-INF/versions/org.apache.httpcomponents/httpcore

Each of these files would contain the version of the corresponding Maven artifact.

Note that this assumes that the plugin is configured to process the right set of dependencies.
The full configuration is shown below:

<!-- MACRO{snippet|id=plugin|file=src/it/intro-sample/pom.xml} -->

For more examples and use cases, see the links in the left-hand side navigation bar.

As suggested by the example shown above, the name template (resp. value template) is always specified by
the `name` parameter (resp. `value` parameter), irrespective of the goal being used.
As we will see below, there is also an `altName` parameter (but no corresponding `altValue` parameter).

### Duplicate key handling

Obviously, the template specified for the key may yield the same value for multiple artifacts.
In some cases it even makes sense to use a fixed value. The plugin joins multiple values for the same key
using a configurable separator. [One of the samples](./examples/bootclasspath.html) shows how this
can be used to join information from multiple artifacts into a single Maven project property.
Note that a duplicate key results in an error if no separator is specified.

### Unresolvable properties and null values

Not all properties are defined for all artifacts. For example, the `classifier` property can be
null and the `bundle.*` properties are only defined for Maven artifacts that are also OSGi bundles.
To deal with properties that can be null or undefined, the following extended syntax is
supported:

    %property?prefix@suffix:default%

If the property is defined, then the prefix and suffix will be added to its value.
If it is undefined or null, then the default value will be used instead. E.g. the following
expression would add the artifact classifier prefixed with a dash:

    %classifier?-@:%

Another approach is to use the `altName` parameter. If the `name` template is unresolvable
(i.e. if one of the expressions refers to an unresolvable property and doesn't specify a default
value), then `altName` will be used instead of `name`. Finally, if `altName` isn't
resolvable either (or isn't specified), then that artifact is simply skipped,

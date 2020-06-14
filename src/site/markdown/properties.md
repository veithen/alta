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

## Supported properties

| Property              | Description                                         |
| --------------------- | --------------------------------------------------- |
| `groupId`             | The Maven group ID                                  |
| `artifactId`          | The Maven artifact ID                               |
| `version`             | The Maven version                                   |
| `classifier`          | The classifier of the artifact (may be null)        |
| `type`                | The artifact type (such as `jar` or `zip`)          |
| `file`                | The full path of the artifact                       |
| `url`                 | A `file://` URL for the artifact                    |
| `bundle.symbolicName` | The symbolic name if the artifact is an OSGi bundle |

Obviously the `file` and `url` properties have values that depend on the local build
environment. They will point to locations in the local Maven repository or the current reactor.
Therefore these properties are typically not used with
[generate-resources](./generate-resources-mojo.html).

As mentioned in the [introduction](./index.html#Unresolvable_properties), the `bundle.*`
properties are only defined for Maven artifacts that are also OSGi bundles.

For artifacts that are part of the reactor, evaluation of the `file`, `url` and
`bundle.*` properties will fail if the `package` phase has not been executed.

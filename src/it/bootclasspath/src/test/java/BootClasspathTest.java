/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2020 Andreas Veithen
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
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.xml.soap.SOAPElement;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

public class BootClasspathTest {
    @Test
    public void testDOM2() {
        // Test that the DOM API is level 2, not level 3
        assertThrows(NoSuchMethodException.class, () -> Node.class.getMethod("getTextContent"));
    }

    @Test
    public void testSAAJ12() {
        // Test that the SAAJ API is version 1.2, not 1.3
        assertThrows(
                NoSuchMethodException.class, () -> SOAPElement.class.getMethod("getElementQName"));
    }
}

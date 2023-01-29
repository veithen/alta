/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2023 Andreas Veithen
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
import static com.google.common.truth.Truth.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void test() throws Exception {
        List<XMLInputFactory> factories = new ArrayList<XMLInputFactory>();
        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(
                                ParserTest.class.getResourceAsStream("parsers.list"), "utf-8"));
        Thread currentThread = Thread.currentThread();
        try {
            ClassLoader savedTCCL = currentThread.getContextClassLoader();
            try {
                String url;
                while ((url = in.readLine()) != null) {
                    ClassLoader classLoader = new URLClassLoader(new URL[] {new URL(url)});
                    currentThread.setContextClassLoader(classLoader);
                    factories.add(XMLInputFactory.newFactory());
                }
            } finally {
                currentThread.setContextClassLoader(savedTCCL);
            }
        } finally {
            in.close();
        }
        assertThat(factories).hasSize(2);
        Class<? extends XMLInputFactory> defaultFactoryClass =
                XMLInputFactory.newFactory().getClass();
        for (XMLInputFactory factory : factories) {
            assertThat(defaultFactoryClass).isNotSameInstanceAs(factory.getClass());
        }
    }
}

/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2024 Andreas Veithen
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
package com.github.veithen.alta.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TemplateTest {
    private TemplateCompiler<Person> templateCompiler;

    @BeforeEach
    public void setUp() {
        templateCompiler = new TemplateCompiler<Person>();
        PropertyGroup<Person, Person> personGroup =
                new PropertyGroup<Person, Person>(Person.class) {
                    @Override
                    public Person prepare(Person object) throws EvaluationException {
                        return object;
                    }
                };
        personGroup.addProperty("givenName", Person::getGivenName);
        personGroup.addProperty("middleName", Person::getMiddleName);
        personGroup.addProperty("surname", Person::getSurname);
        templateCompiler.setDefaultPropertyGroup(personGroup);
        PropertyGroup<Person, Address> addressGroup =
                new PropertyGroup<Person, Address>(Address.class) {
                    @Override
                    public Address prepare(Person object) throws EvaluationException {
                        return object.getAddress();
                    }
                };
        addressGroup.addProperty("street", Address::getStreet);
        addressGroup.addProperty("city", Address::getCity);
        templateCompiler.addPropertyGroup("address", addressGroup);
    }

    @Test
    public void testSimple() throws Exception {
        Person person = new Person("Roy", null, "Manning", new Address("High street", "Dummytown"));
        assertThat(
                        templateCompiler
                                .compile("%givenName% %surname% lives in %address.city%")
                                .evaluate(person))
                .isEqualTo("Roy Manning lives in Dummytown");
    }

    @Test
    public void testPropertyNotSupported() throws Exception {
        Person person = new Person("Albert", null, "Einstein", null);
        assertThat(templateCompiler.compile("%address.street% %address.city%").evaluate(person))
                .isNull();
    }

    @Test
    public void testConditional() throws Exception {
        Template<Person> template =
                templateCompiler.compile("%givenName%%middleName? (@):% %surname%");
        assertThat(template.evaluate(new Person("Albert", null, "Einstein", null)))
                .isEqualTo("Albert Einstein");
        assertThat(template.evaluate(new Person("John", "Trevor", "Mulder", null)))
                .isEqualTo("John (Trevor) Mulder");
    }
}

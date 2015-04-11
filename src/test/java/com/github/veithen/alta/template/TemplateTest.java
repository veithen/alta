/*
 * #%L
 * Alta Maven Plugin
 * %%
 * Copyright (C) 2014 - 2015 Andreas Veithen
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.github.veithen.alta.template.EvaluationException;
import com.github.veithen.alta.template.Property;
import com.github.veithen.alta.template.PropertyGroup;
import com.github.veithen.alta.template.TemplateCompiler;

public class TemplateTest {
    private TemplateCompiler<Person> templateCompiler;
    
    @Before
    public void setUp() {
        templateCompiler = new TemplateCompiler<Person>();
        PropertyGroup<Person,Person> personGroup = new PropertyGroup<Person,Person>(Person.class) {
            @Override
            public Person prepare(Person object) throws EvaluationException {
                return object;
            }
        };
        personGroup.addProperty("givenName", new Property<Person>() {
            public String evaluate(Person groupContext) {
                return groupContext.getGivenName();
            }
        });
        personGroup.addProperty("surname", new Property<Person>() {
            public String evaluate(Person groupContext) {
                return groupContext.getSurname();
            }
        });
        templateCompiler.setDefaultPropertyGroup(personGroup);
        PropertyGroup<Person,Address> addressGroup = new PropertyGroup<Person,Address>(Address.class) {
            @Override
            public Address prepare(Person object) throws EvaluationException {
                return object.getAddress();
            }
        };
        addressGroup.addProperty("street", new Property<Address>() {
            public String evaluate(Address groupContext) {
                return groupContext.getStreet();
            }
        });
        addressGroup.addProperty("city", new Property<Address>() {
            public String evaluate(Address groupContext) {
                return groupContext.getCity();
            }
        });
        templateCompiler.addPropertyGroup("address", addressGroup);
    }
    
    @Test
    public void test() throws Exception {
        Person person = new Person("Roy", "Manning", new Address("High street", "Dummytown"));
        assertEquals("Roy Manning lives in Dummytown", templateCompiler.compile("%givenName% %surname% lives in %address.city%").evaluate(person));
    }
    
    @Test
    public void testPropertyNotSupported() throws Exception {
        Person person = new Person("Albert", "Einstein", null);
        assertNull(templateCompiler.compile("%address.street% %address.city%").evaluate(person));
    }
}

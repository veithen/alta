package com.github.veithen.alta.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class PatternTest {
    private PatternCompiler<Person> patternCompiler;
    
    @Before
    public void setUp() {
        patternCompiler = new PatternCompiler<Person>();
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
        patternCompiler.setDefaultPropertyGroup(personGroup);
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
        patternCompiler.addPropertyGroup("address", addressGroup);
    }
    
    @Test
    public void test() throws Exception {
        Person person = new Person("Roy", "Manning", new Address("High street", "Dummytown"));
        assertEquals("Roy Manning lives in Dummytown", patternCompiler.compile("%givenName% %surname% lives in %address.city%").evaluate(person));
    }
    
    @Test
    public void testPropertyNotSupported() throws Exception {
        Person person = new Person("Albert", "Einstein", null);
        assertNull(patternCompiler.compile("%address.street% %address.city%").evaluate(person));
    }
}

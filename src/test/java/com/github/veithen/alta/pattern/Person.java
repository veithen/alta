package com.github.veithen.alta.pattern;

public class Person {
    private final String givenName;
    private final String surname;
    private final Address address;
    
    public Person(String givenName, String surname, Address address) {
        this.givenName = givenName;
        this.surname = surname;
        this.address = address;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public Address getAddress() {
        return address;
    }
}

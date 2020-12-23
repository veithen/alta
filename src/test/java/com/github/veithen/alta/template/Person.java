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
package com.github.veithen.alta.template;

public class Person {
    private final String givenName;
    private final String middleName;
    private final String surname;
    private final Address address;

    public Person(String givenName, String middleName, String surname, Address address) {
        this.givenName = givenName;
        this.middleName = middleName;
        this.surname = surname;
        this.address = address;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getSurname() {
        return surname;
    }

    public Address getAddress() {
        return address;
    }
}

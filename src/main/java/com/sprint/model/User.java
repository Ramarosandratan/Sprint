package com.sprint.model;

import com.sprint.annotation.ParamField;

public class User {
    @ParamField("first_name")
    private String firstName;
    
    @ParamField
    private String lastName;
    
    @ParamField
    private int age;

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "User [firstName=" + firstName + ", lastName=" + lastName + ", age=" + age + "]";
    }
}

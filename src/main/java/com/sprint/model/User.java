package com.sprint.model;

import com.sprint.annotation.ParamField;

public class User {
    @ParamField("name") // Map "name" parameter to firstName
    private String firstName;
    
    @ParamField
    private String lastName;
    
    @ParamField
    private String email; // New email field
    
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "User [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", age=" + age + "]";
    }
}

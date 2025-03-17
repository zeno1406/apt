package com.example.shop;

public class StudentInfo {
     private String firstName;
     private String lastName;
     private String mssv;
     private int age;

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

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public StudentInfo() {
    }

    public StudentInfo(String firstName, String lastName, String mssv, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mssv = mssv;
        this.age = age;
    }
}

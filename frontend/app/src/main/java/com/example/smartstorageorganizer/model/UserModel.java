package com.example.smartstorageorganizer.model;

public class UserModel {
    private String date;
    private String name;
    private String surname;
    private String email;
    private String status;

    public UserModel() {}

    public UserModel(String date, String name, String status) {
        this.date = date;
        this.name = name;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }


    public String getStatus() {
        return status;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}



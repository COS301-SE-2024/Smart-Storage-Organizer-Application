package com.example.smartstorageorganizer.model;

public class UserModel {
    private String date;
    private String name;
    private String status;

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
}



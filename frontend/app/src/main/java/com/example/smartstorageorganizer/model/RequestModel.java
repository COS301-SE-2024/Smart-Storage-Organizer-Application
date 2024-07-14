package com.example.smartstorageorganizer.model;

public class RequestModel {
    private String date;
    private String name;
    private String description;
    private String status;

    public RequestModel(String date, String name, String description, String status) {
        this.date = date;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }
}


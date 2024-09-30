package com.example.smartstorageorganizer;

public class NotificationModel {
    private String title;
    private String message;
    private String date;
    private Boolean isRead;

    public NotificationModel(String title, String message, String date, Boolean isRead) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.isRead = isRead;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public Boolean isRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}

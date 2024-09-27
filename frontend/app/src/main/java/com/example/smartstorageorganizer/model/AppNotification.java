package com.example.smartstorageorganizer.model;

import java.util.Date;

public class AppNotification {
    private String title;
    private String content;
    private Date date;
    private boolean isRead;

    public AppNotification() {
        // Default constructor required for calls to DataSnapshot.getValue(Notification.class)
    }

    public AppNotification(String content, Date date, boolean isRead) {
        this.content = content;
        this.date = date;
        this.isRead = isRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
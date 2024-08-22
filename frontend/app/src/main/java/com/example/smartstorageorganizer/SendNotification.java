package com.example.smartstorageorganizer;

public class SendNotification {
    private String to;
    private NotificationBody notification;

    public SendNotification(String to, NotificationBody notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationBody getNotification() {
        return notification;
    }

    public void setNotification(NotificationBody notification) {
        this.notification = notification;
    }
}

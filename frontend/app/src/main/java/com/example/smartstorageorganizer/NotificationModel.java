package com.example.smartstorageorganizer;

public class NotificationModel {
    private String title;
    private String message;
    private String date;
    private Boolean isRead;
    private String userRole;
    private String organizationId;

    public NotificationModel(String title, String message, String date, Boolean isRead, String userRole, String organizationId) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.isRead = isRead;
        this.userRole = userRole;
        this.organizationId = organizationId;
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

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}

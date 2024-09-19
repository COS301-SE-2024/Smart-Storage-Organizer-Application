package com.example.smartstorageorganizer.model;

public class LogEntry {
    private String userName;
    private String actionDone;
    private String itemActedUpon;
    private String details;
    private String previousDetails;
    private String newDetails;
    private boolean expanded;

    public LogEntry(String userName, String actionDone, String itemActedUpon, String details, String previousDetails, String newDetails) {
        this.userName = userName;
        this.actionDone = actionDone;
        this.itemActedUpon = itemActedUpon;
        this.details = details;
        this.previousDetails = previousDetails;
        this.newDetails = newDetails;
        this.expanded = false;
    }

    public String getUserName() {
        return userName;
    }

    public String getActionDone() {
        return actionDone;
    }

    public String getItemActedUpon() {
        return itemActedUpon;
    }

    public String getDetails() {
        return details;
    }

    public String getPreviousDetails() {
        return previousDetails;
    }

    public String getNewDetails() {
        return newDetails;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}


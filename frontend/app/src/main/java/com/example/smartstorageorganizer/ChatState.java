package com.example.smartstorageorganizer;

public class ChatState {

    private boolean isEnteringToken;
    private String remoteToken;
    private String messageText;

    // Default constructor
    public ChatState() {
        this.isEnteringToken = true;
        this.remoteToken = "";
        this.messageText = "";
    }

    // Constructor with parameters
    public ChatState(boolean isEnteringToken, String remoteToken, String messageText) {
        this.isEnteringToken = isEnteringToken;
        this.remoteToken = remoteToken;
        this.messageText = messageText;
    }

    public ChatState(ChatState state) {
    }

    // Getters and Setters
    public boolean isEnteringToken() {
        return isEnteringToken;
    }

    public void setEnteringToken(boolean enteringToken) {
        isEnteringToken = enteringToken;
    }

    public String getRemoteToken() {
        return remoteToken;
    }

    public void setRemoteToken(String remoteToken) {
        this.remoteToken = remoteToken;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}

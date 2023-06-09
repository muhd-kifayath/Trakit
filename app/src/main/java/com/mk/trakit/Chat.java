package com.mk.trakit;

public class Chat {

    private String messageText, receiverUserId, senderUserId, currentDate, currentTime;

    public Chat(String messageText, String receiverUserId, String senderUserId, String currentDate, String currentTime) {
        this.messageText = messageText;
        this.receiverUserId = receiverUserId;
        this.senderUserId = senderUserId;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public Chat() {
    }

    public String getMessageText() {
        return messageText;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
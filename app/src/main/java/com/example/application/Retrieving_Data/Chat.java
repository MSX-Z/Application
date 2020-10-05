package com.example.application.Retrieving_Data;

import com.google.firebase.database.PropertyName;

public class Chat {
    private String message, receiver, sender, timestamp;
    private boolean isSeen;

    public Chat(){};

    public Chat(String message, String receiver, String sender, String timestamp, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @PropertyName("isSeen")
    public boolean isSeen() {
        return isSeen;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName("isSeen")
    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "message='" + message + '\'' +
                ", receiver='" + receiver + '\'' +
                ", sender='" + sender + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", isSeen=" + isSeen +
                '}';
    }
}

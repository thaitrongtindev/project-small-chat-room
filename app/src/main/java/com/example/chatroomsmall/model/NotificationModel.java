package com.example.chatroomsmall.model;

public class NotificationModel {

    private String title;
    private String message;

    public NotificationModel() {
    }

    public NotificationModel(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

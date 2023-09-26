package com.example.chatroomsmall.model;

public class ChatModel {

    private String message;
    private String user_name;
    private String messageId;
    private String user_image_url;
    private String chat_image;

    public ChatModel() {
    }

    public ChatModel(String message, String user_name, String messageId, String user_image_url, String chat_image) {
        this.message = message;
        this.user_name = user_name;
        this.messageId = messageId;
        this.user_image_url = user_image_url;
        this.chat_image = chat_image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUser_image_url() {
        return user_image_url;
    }

    public void setUser_image_url(String user_image_url) {
        this.user_image_url = user_image_url;
    }

    public String getChat_image() {
        return chat_image;
    }

    public void setChat_image(String chat_image) {
        this.chat_image = chat_image;
    }
}

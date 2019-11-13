package com.example.chatapp.friends;

public class User {
    private String id;
    private String username;
    private String imgUrl;
    private String status;

    public User(String id, String username, String status, String imgUrl) {
        this.id = id;
        this.username = username;
        this.imgUrl = imgUrl;
        this.status = status;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getStatus() {
        return status;
    }
}

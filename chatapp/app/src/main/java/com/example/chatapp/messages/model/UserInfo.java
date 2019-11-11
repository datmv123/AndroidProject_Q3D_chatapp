package com.example.chatapp.messages.model;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserInfo {
    private String id;
    private String username;
    private String imgUrl;
    private Map<String, String> friends = new HashMap<>(); // friendUID - date become friends
    private String status;

    public UserInfo() {
    }

    public UserInfo(String uid, String username, String imgUrl, Map<String, String> friends, String status) {
        this.id = uid;
        this.username = username;
        this.imgUrl = imgUrl;
        this.friends = friends;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String uid) {
        this.id = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof UserInfo){
            return ((UserInfo) obj).id.equals(this.id);
        }
        return false;
    }
}

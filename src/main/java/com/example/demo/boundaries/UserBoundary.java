package com.example.demo.boundaries;

import java.util.HashMap;
import java.util.Map;

public class UserBoundary {
    private Map<String, String> userId;

    private String role;
    private String username;
    private String avatar;

    public UserBoundary() {
    }

    public UserBoundary(String email, String role, String username, String avatar) {
        this.role = role;
        this.username = username;
        this.avatar = avatar;
        this.userId = new HashMap<String, String>();
        this.userId.put("domain", "example.com");
        this.userId.put("email", email);
    }

    public UserBoundary(NewUserBoundary newUser) {
        // this.email = email;
        this.role = newUser.getRole();
        this.username = newUser.getUsername();
        this.avatar = newUser.getAvatar();
        this.userId = new HashMap<String, String>();
        this.userId.put("domain", "example.com");
        this.userId.put("email", newUser.getEmail());
    }

    public Map<String, String> getUserId() {
        return this.userId;
    }

    public void setUserId(Map<String, String> userId) {
        this.userId = userId;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}

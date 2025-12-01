package com.example.demo.dtos;

import java.io.Serializable;
import java.util.UUID;

public class UserSyncDTO implements Serializable {
    private UUID userId;
    private String action; // "CREATE", "DELETE"
    private UserDetailsDTO userData; // Datele user-ului

    public UserSyncDTO() {
    }

    public UserSyncDTO(UUID userId, String action, UserDetailsDTO userData) {
        this.userId = userId;
        this.action = action;
        this.userData = userData;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public UserDetailsDTO getUserData() {
        return userData;
    }

    public void setUserData(UserDetailsDTO userData) {
        this.userData = userData;
    }

    @Override
    public String toString() {
        return "UserSyncDTO{" +
                "userId=" + userId +
                ", action='" + action + '\'' +
                '}';
    }
}
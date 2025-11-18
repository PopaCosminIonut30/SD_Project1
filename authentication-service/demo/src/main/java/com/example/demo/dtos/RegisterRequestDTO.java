package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

// DTO pentru a înregistra o parolă
public class RegisterRequestDTO {

    @NotNull(message = "userId is required")
    private UUID userId; // ID-ul user-ului din user-service

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "role is required")
    private String role;

    // Getteri și Setteri
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}
package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;

// DTO pentru cererea de login
public class LoginRequestDTO {
    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;

    // Getteri și Setteri
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
}
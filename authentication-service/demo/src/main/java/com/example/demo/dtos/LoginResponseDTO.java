package com.example.demo.dtos;

// DTO pentru răspunsul de login
public class LoginResponseDTO {
    private String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    // Getter și Setter
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
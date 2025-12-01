package com.example.demo.dtos;

import java.util.UUID;

// Această clasă este folosită doar pentru deserializarea mesajelor de sincronizare
public class UserDetailsDTO {

    private UUID id;
    private String name;
    private String address;
    private String username;
    private String role;
    private Integer age;

    public UserDetailsDTO() {
    }

    // Getteri și Setteri sunt esențiali pentru JSON deserialization
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
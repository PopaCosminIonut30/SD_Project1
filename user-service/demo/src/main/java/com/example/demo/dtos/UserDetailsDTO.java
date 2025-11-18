package com.example.demo.dtos;


import com.example.demo.dtos.validators.annotation.AgeLimit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class UserDetailsDTO {

    private UUID id;

    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "role is required")
    private String role;

    @NotNull(message = "age is required")
    @AgeLimit(value = 18)
    private Integer age;



    public UserDetailsDTO() {
    }

    public UserDetailsDTO(String name, String address, int age, String role, String username) {
        this.name = name;
        this.address = address;
        this.age = age;
        this.role = role;
        this.username = username;
    }

    public UserDetailsDTO(UUID id, String name, String address, int age, String role, String username) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
        this.role = role;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {this.role = role;}
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsDTO that = (UserDetailsDTO) o;
        return age == that.age &&
                Objects.equals(name, that.name) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, age);
    }
}

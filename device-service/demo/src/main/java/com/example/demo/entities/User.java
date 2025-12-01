package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "users") // Tabelă locală în device_db
public class User implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "username")
    private String username;

    public User() {
    }

    public User(UUID id) {
        this.id = id;
    }

    // --- (NOU) Constructorul care lipsea ---
    public User(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
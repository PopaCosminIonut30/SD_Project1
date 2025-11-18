package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;


import java.io.Serializable;
import java.util.UUID;

// Device.java

@Table(name = "devices")
@Entity
public class Device implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description") // Atribut adițional (opțional)
    private String description;

    @Column(name = "address") // Atribut adițional (opțional)
    private String address;

    @Column(name = "max_consumption", nullable = false)
    private double maxConsumption; // Cerința din PDF

    @Column(name = "user_id") // Pentru maparea "Assign devices to users"
    private UUID userId;

    // Constructor gol
    public Device() {}

    // Constructor cu parametri
    public Device(String name, String description, String address, double maxConsumption, UUID userId) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.maxConsumption = maxConsumption;
        this.userId = userId;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public double getMaxConsumption() {
        return maxConsumption;
    }
    public void setMaxConsumption(double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
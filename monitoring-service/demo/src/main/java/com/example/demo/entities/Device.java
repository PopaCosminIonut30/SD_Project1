package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "devices") // Tabela locală în monitoring_db
public class Device implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "max_consumption")
    private Double maxConsumption;

    @Column(name = "user_id")
    private UUID userId;

    public Device() {}

    public Device(UUID id, Double maxConsumption, UUID userId) {
        this.id = id;
        this.maxConsumption = maxConsumption;
        this.userId = userId;
    }

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(Double maxConsumption) { this.maxConsumption = maxConsumption; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
}
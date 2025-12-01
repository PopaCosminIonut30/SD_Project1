package com.example.demo.dtos;

import java.io.Serializable;
import java.util.UUID;

public class DeviceSyncDTO implements Serializable {
    private UUID id;
    private Double maxConsumption;
    private UUID userId; // Cui îi aparține
    private String action; // "CREATE", "DELETE", "UPDATE"

    public DeviceSyncDTO() {
    }

    public DeviceSyncDTO(UUID id, Double maxConsumption, UUID userId, String action) {
        this.id = id;
        this.maxConsumption = maxConsumption;
        this.userId = userId;
        this.action = action;
    }

    // Getteri și Setteri
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(Double maxConsumption) { this.maxConsumption = maxConsumption; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
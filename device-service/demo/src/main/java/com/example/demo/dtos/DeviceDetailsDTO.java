package com.example.demo.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class DeviceDetailsDTO {

    private UUID id;

    @NotBlank(message = "name is required")
    private String name;

    // Am scos @NotBlank, descrierea poate fi goală
    private String description;

    @NotBlank(message = "address is required")
    private String address; // Corectat din "Adrress"

    @NotNull(message = "maxConsumption is required")
    @Min(value = 0, message = "maxConsumption must be non-negative")
    private Double maxConsumption; // Corectat din "double" în "Double" pentru @NotNull

    @NotNull(message = "userId is required")
    private UUID userId;

    public DeviceDetailsDTO() {
    }

    public DeviceDetailsDTO(String name, String description, String address, Double maxConsumption, UUID userId) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.maxConsumption = maxConsumption;
        this.userId = userId;
    }

    public DeviceDetailsDTO(UUID id, String name, String description, String address, Double maxConsumption, UUID userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.maxConsumption = maxConsumption;
        this.userId = userId;
    }

    // --- Getteri și Setteri (Corectați) ---

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

    public Double getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(Double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDetailsDTO that = (DeviceDetailsDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(address, that.address) &&
                Objects.equals(maxConsumption, that.maxConsumption) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, address, maxConsumption, userId);
    }
}
package com.example.demo.controllers;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.services.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
@Validated
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.findDevices());
    }

    // --- (MODIFICAT) Acum returnează DeviceDetailsDTO în loc de Void ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDetailsDTO> create(@Valid @RequestBody DeviceDetailsDTO device) {
        // --- (MODIFICAT) Primește DTO-ul creat ---
        DeviceDetailsDTO createdDeviceDTO = deviceService.insert(device); // Acum 'insert' returnează DTO

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDeviceDTO.getId()) // Folosește ID-ul din DTO
                .toUri();

        // --- (MODIFICAT) Returnează 201 + Location + Body-ul (care conține ID-ul) ---
        return ResponseEntity.created(location).body(createdDeviceDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findDeviceById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDetailsDTO> updateDevice(@PathVariable UUID id, @Valid @RequestBody DeviceDetailsDTO deviceDetailsDTO) {
        DeviceDetailsDTO updatedDevice = deviceService.updateDevice(id, deviceDetailsDTO);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<DeviceDTO>> getDevicesForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(deviceService.findDevicesByUserId(userId));
    }
}
package com.example.demo.repositories;

import com.example.demo.entities.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    // Aici vom adăuga metode de query pentru istoric mai târziu
    List<SensorData> findByDeviceId(UUID deviceId);
}
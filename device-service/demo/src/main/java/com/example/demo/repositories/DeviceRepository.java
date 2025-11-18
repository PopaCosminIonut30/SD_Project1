package com.example.demo.repositories;

import com.example.demo.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * Numele metodei "findByUserId" (cu 'u' mic)
     * trebuie să se potrivească perfect cu câmpul "private UUID userId"
     * din entitatea ta Device.java
     */
    List<Device> findByUserId(UUID userId);

}
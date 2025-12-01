package com.example.demo.services;

import com.example.demo.config.RabbitMQConfig; // Import config
import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.dtos.DeviceSyncDTO; // Import DTO
import com.example.demo.dtos.builders.DeviceBuilder;
import com.example.demo.entities.Device;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate; // Import RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final RabbitTemplate rabbitTemplate; // Injectare

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, RabbitTemplate rabbitTemplate) {
        this.deviceRepository = deviceRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findDeviceById(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(deviceOptional.get());
    }

    public DeviceDetailsDTO insert(DeviceDetailsDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());

        // --- (NOU) Trimite mesaj SYNC CREATE ---
        try {
            DeviceSyncDTO syncMessage = new DeviceSyncDTO(
                    device.getId(),
                    device.getMaxConsumption(),
                    device.getUserId(),
                    "CREATE"
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.DEVICE_EXCHANGE, RabbitMQConfig.DEVICE_ROUTING_KEY, syncMessage);
            LOGGER.info("Sent SYNC CREATE message for device id: {}", device.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to send sync message for device id: {}", device.getId(), e);
        }

        return DeviceBuilder.toDeviceDetailsDTO(device);
    }

    public DeviceDetailsDTO updateDevice(UUID id, DeviceDetailsDTO deviceDetailsDTO) {
        Device deviceToUpdate = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Device with id {} was not found in db", id);
                    return new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
                });

        deviceToUpdate.setName(deviceDetailsDTO.getName());
        deviceToUpdate.setAddress(deviceDetailsDTO.getAddress());
        deviceToUpdate.setDescription(deviceDetailsDTO.getDescription());
        deviceToUpdate.setMaxConsumption(deviceDetailsDTO.getMaxConsumption());
        deviceToUpdate.setUserId(deviceDetailsDTO.getUserId());

        Device updatedDevice = deviceRepository.save(deviceToUpdate);
        LOGGER.debug("Device with id {} was updated in db", updatedDevice.getId());

        // Opțional: poți trimite mesaj UPDATE dacă vrei să actualizezi maxConsumption și în Monitoring
        try {
            DeviceSyncDTO syncMessage = new DeviceSyncDTO(
                    updatedDevice.getId(),
                    updatedDevice.getMaxConsumption(),
                    updatedDevice.getUserId(),
                    "UPDATE"
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.DEVICE_EXCHANGE, RabbitMQConfig.DEVICE_ROUTING_KEY, syncMessage);
        } catch (Exception e) {
            LOGGER.error("Failed to send sync update message", e);
        }

        return DeviceBuilder.toDeviceDetailsDTO(updatedDevice);
    }

    public void deleteDevice(UUID id) {
        if (!deviceRepository.existsById(id)) {
            LOGGER.error("Device with id {} was not found in db for deletion", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        deviceRepository.deleteById(id);
        LOGGER.debug("Device with id {} was deleted from db", id);

        // --- (NOU) Trimite mesaj SYNC DELETE ---
        try {
            DeviceSyncDTO syncMessage = new DeviceSyncDTO(id, null, null, "DELETE");
            rabbitTemplate.convertAndSend(RabbitMQConfig.DEVICE_EXCHANGE, RabbitMQConfig.DEVICE_ROUTING_KEY, syncMessage);
            LOGGER.info("Sent SYNC DELETE message for device id: {}", id);
        } catch (Exception e) {
            LOGGER.error("Failed to send sync delete message for device id: {}", id, e);
        }
    }

    public List<DeviceDTO> findDevicesByUserId(UUID userId) {
        List<Device> deviceList = deviceRepository.findByUserId(userId);
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }
}
package com.example.demo.consumer;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.DeviceSyncDTO;
import com.example.demo.entities.Device;
import com.example.demo.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeviceSyncConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSyncConsumer.class);
    private final DeviceRepository deviceRepository;

    public DeviceSyncConsumer(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.DEVICE_SYNC_QUEUE)
    public void consumeSyncMessage(DeviceSyncDTO message) {
        LOGGER.info("Received Device Sync Message: {}", message);

        try {
            if ("CREATE".equals(message.getAction()) || "UPDATE".equals(message.getAction())) {
                Device device = new Device(message.getId(), message.getMaxConsumption(), message.getUserId());
                deviceRepository.save(device);
                LOGGER.info("Synced Device in monitoring_db: {}", message.getId());
            } else if ("DELETE".equals(message.getAction())) {
                if (deviceRepository.existsById(message.getId())) {
                    deviceRepository.deleteById(message.getId());
                    LOGGER.info("Deleted Device from monitoring_db: {}", message.getId());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error syncing device: {}", e.getMessage());
        }
    }
}
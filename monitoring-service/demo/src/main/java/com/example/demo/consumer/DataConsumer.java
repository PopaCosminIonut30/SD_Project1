package com.example.demo.consumer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.example.demo.dtos.NotificationDTO;
import com.example.demo.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.MeasurementDTO;
import com.example.demo.entities.SensorData;
import com.example.demo.repositories.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DataConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataConsumer.class);
    private final SensorDataRepository sensorDataRepository;
    private final DeviceRepository deviceRepository; // Avem nevoie de limitele device-urilor
    private final RabbitTemplate rabbitTemplate; // Pentru a trimite alerta la WebSocket Service

    public DataConsumer(SensorDataRepository sensorDataRepository,
                        DeviceRepository deviceRepository,
                        RabbitTemplate rabbitTemplate) {
        this.sensorDataRepository = sensorDataRepository;
        this.deviceRepository = deviceRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.DATA_QUEUE)
    public void consumeMessage(MeasurementDTO message) {
        LOGGER.info("Received Measurement: {}", message);

        // 1. Salvare în baza de date (A2)
        SensorData data = new SensorData(
                message.getTimestamp(),
                message.getDeviceId(),
                message.getMeasurementValue()
        );
        sensorDataRepository.save(data);

        // 2. CĂUTARE SIMPLĂ (fără UUID.fromString)
        // message.getDeviceId() este deja String, iar Repository-ul tău vrea String.
        deviceRepository.findById(message.getDeviceId()).ifPresent(device -> {
            if (message.getMeasurementValue() > device.getMaxConsumption()) {
                NotificationDTO alert = new NotificationDTO(
                        device.getId().toString(),
                        "ALERTA: Consumul de " + message.getMeasurementValue() +
                                " a depasit limita de " + device.getMaxConsumption(),
                        System.currentTimeMillis()
                );

                rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, alert);
                LOGGER.warn("Overconsumption alert sent for device: {}", device.getId());
            }
        });
    }
}
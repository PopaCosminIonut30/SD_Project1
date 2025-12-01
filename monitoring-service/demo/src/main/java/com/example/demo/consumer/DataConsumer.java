package com.example.demo.consumer;

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

    public DataConsumer(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.DATA_QUEUE)
    public void consumeMessage(MeasurementDTO message) {
        LOGGER.info("Received Measurement: {}", message);

        // Salvare în baza de date
        SensorData data = new SensorData(
                message.getTimestamp(),
                message.getDeviceId(),
                message.getMeasurementValue()
        );
        sensorDataRepository.save(data);
        LOGGER.info("Data saved to monitoring_db");
    }
}
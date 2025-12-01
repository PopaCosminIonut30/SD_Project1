package com.example.demo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DATA_QUEUE = "device_data_queue";
    // (NOU) Coada de sincronizare
    public static final String DEVICE_SYNC_QUEUE = "sync_device_queue";

    @Bean
    public Queue dataQueue() {
        return new Queue(DATA_QUEUE);
    }

    @Bean
    public Queue deviceSyncQueue() {
        return new Queue(DEVICE_SYNC_QUEUE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
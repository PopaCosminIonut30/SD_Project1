package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier; // Import NOU
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- USER SYNC (Consumer) ---
    public static final String USER_SYNC_QUEUE = "sync_user_queue";
    public static final String USER_EXCHANGE = "user_exchange";
    public static final String USER_ROUTING_KEY = "user_routing_key";

    @Bean(name = "userQueue") // Nume explicit
    public Queue userQueue() {
        return new Queue(USER_SYNC_QUEUE);
    }

    @Bean(name = "userExchange") // Nume explicit
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    // Binding pentru USER
    @Bean
    public Binding userBinding(@Qualifier("userQueue") Queue queue,
                               @Qualifier("userExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(USER_ROUTING_KEY);
    }

    // --- DEVICE SYNC (Producer) ---
    public static final String DEVICE_SYNC_QUEUE = "sync_device_queue";
    public static final String DEVICE_EXCHANGE = "device_exchange";
    public static final String DEVICE_ROUTING_KEY = "device_routing_key";

    @Bean(name = "deviceQueue") // Nume explicit
    public Queue deviceQueue() {
        return new Queue(DEVICE_SYNC_QUEUE);
    }

    @Bean(name = "deviceExchange") // Nume explicit
    public TopicExchange deviceExchange() {
        return new TopicExchange(DEVICE_EXCHANGE);
    }

    // Binding pentru DEVICE
    @Bean
    public Binding deviceBinding(@Qualifier("deviceQueue") Queue queue,
                                 @Qualifier("deviceExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEVICE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
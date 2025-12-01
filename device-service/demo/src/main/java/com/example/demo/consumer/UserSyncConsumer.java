package com.example.demo.consumer;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.UserSyncDTO;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserSyncConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncConsumer.class);
    private final UserRepository userRepository;

    public UserSyncConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_SYNC_QUEUE)
    public void consumeUserSyncMessage(UserSyncDTO message) {
        LOGGER.info("Received User Sync Message: {}", message);

        try {
            if ("CREATE".equals(message.getAction())) {
                // Salvăm user-ul în baza locală
                User localUser = new User(
                        message.getUserId(),
                        // Dacă UserDetailsDTO e null (ex. la un update parțial), punem un placeholder
                        message.getUserData() != null ? message.getUserData().getUsername() : "unknown"
                );
                userRepository.save(localUser);
                LOGGER.info("Synced (Saved) User with ID: {}", message.getUserId());

            } else if ("DELETE".equals(message.getAction())) {
                // Ștergem user-ul din baza locală
                if (userRepository.existsById(message.getUserId())) {
                    userRepository.deleteById(message.getUserId());
                    LOGGER.info("Synced (Deleted) User with ID: {}", message.getUserId());

                    // TODO: Opțional, aici ai putea șterge și toate Device-urile asociate acestui user
                    // deviceRepository.deleteByUserId(message.getUserId());
                } else {
                    LOGGER.warn("User to delete not found in local DB: {}", message.getUserId());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error processing sync message: {}", e.getMessage());
        }
    }
}
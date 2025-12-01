package com.example.demo.services;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.UserDetailsDTO;
import com.example.demo.dtos.UserSyncDTO; // <-- Importăm noul DTO
import com.example.demo.dtos.builders.UserBuilder;
import com.example.demo.entities.User;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate; // Necesar pentru RabbitMQ

    @Autowired
    public UserService(UserRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<UserDTO> findUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDetailsDTO findUserById(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDetailsDTO(userOptional.get());
    }

    public UserDetailsDTO insert(UserDetailsDTO userDTO) {
        // 1. Salvare în DB
        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());

        // Convertim entitatea salvată înapoi în DTO (ca să avem ID-ul generat)
        UserDetailsDTO savedUserDTO = UserBuilder.toUserDetailsDTO(user);

        // 2. Sincronizare RabbitMQ (CREATE)
        try {
            // Creăm mesajul de sincronizare folosind DTO-urile tale
            UserSyncDTO syncMessage = new UserSyncDTO(user.getId(), "CREATE", savedUserDTO);

            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EXCHANGE, RabbitMQConfig.USER_ROUTING_KEY, syncMessage);
            LOGGER.info("Sent SYNC CREATE message for user id: {}", user.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to send sync message for user id: {}", user.getId(), e);
        }

        return savedUserDTO;
    }

    public UserDetailsDTO updateUser(UUID id, UserDetailsDTO userDetailsDTO) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} was not found in db", id);
                    return new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
                });

        userToUpdate.setName(userDetailsDTO.getName());
        userToUpdate.setAddress(userDetailsDTO.getAddress());
        userToUpdate.setAge(userDetailsDTO.getAge());
        userToUpdate.setUsername(userDetailsDTO.getUsername());
        userToUpdate.setRole(userDetailsDTO.getRole());

        User updatedUser = userRepository.save(userToUpdate);
        LOGGER.debug("User with id {} was updated in db", updatedUser.getId());

        // Opțional: Poți adăuga sincronizare și aici cu action="UPDATE" dacă este cerut

        return UserBuilder.toUserDetailsDTO(updatedUser);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            LOGGER.error("User with id {} was not found in db for deletion", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        // 1. Ștergere din DB
        userRepository.deleteById(id);
        LOGGER.debug("User with id {} was deleted from db", id);

        // 2. Sincronizare RabbitMQ (DELETE)
        try {
            // Trimitem doar ID-ul și acțiunea DELETE
            UserSyncDTO syncMessage = new UserSyncDTO(id, "DELETE");

            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EXCHANGE, RabbitMQConfig.USER_ROUTING_KEY, syncMessage);
            LOGGER.info("Sent SYNC DELETE message for user id: {}", id);
        } catch (Exception e) {
            LOGGER.error("Failed to send sync delete message for user id: {}", id, e);
        }
    }
}
package com.example.demo.services;

// ... (importurile existente) ...
import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.UserDetailsDTO;
import com.example.demo.dtos.builders.UserBuilder;
import com.example.demo.entities.User;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    // ... (LOGGER și constructorul neschimbate) ...
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findUsers() {
        // ... (neschimbat) ...
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDetailsDTO findUserById(UUID id) {
        // ... (neschimbat) ...
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDetailsDTO(userOptional.get());
    }

    // --- (MODIFICAT) Acum returnează UserDetailsDTO în loc de UUID ---
    public UserDetailsDTO insert(UserDetailsDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());
        // --- (MODIFICAT) Returnează DTO-ul complet ---
        return UserBuilder.toUserDetailsDTO(user);
    }

    public UserDetailsDTO updateUser(UUID id, UserDetailsDTO userDetailsDTO) {
        // ... (neschimbat) ...
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
        return UserBuilder.toUserDetailsDTO(updatedUser);
    }

    public void deleteUser(UUID id) {
        // ... (neschimbat) ...
        if (!userRepository.existsById(id)) {
            LOGGER.error("User with id {} was not found in db for deletion", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        userRepository.deleteById(id);
        LOGGER.debug("User with id {} was deleted from db", id);
    }
}
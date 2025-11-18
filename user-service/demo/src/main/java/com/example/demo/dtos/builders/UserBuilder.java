package com.example.demo.dtos.builders;

import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.UserDetailsDTO;
import com.example.demo.entities.User;

public class UserBuilder {

    private UserBuilder() {
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getAge(), user.getUsername(), user.getRole());
    }

    public static UserDetailsDTO toUserDetailsDTO(User user) {
        return new UserDetailsDTO(user.getId(), user.getName(), user.getAddress(), user.getAge(), user.getRole(), user.getUsername());
    }

    public static User toEntity(UserDetailsDTO userDetailsDTO) {
        return new User(userDetailsDTO.getName(),
                userDetailsDTO.getAddress(),
                userDetailsDTO.getAge(),
                userDetailsDTO.getUsername(),
                userDetailsDTO.getRole());
    }
}

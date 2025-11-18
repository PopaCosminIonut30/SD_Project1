package com.example.demo.controllers;

import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.UserDetailsDTO;
import com.example.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getPeople() {
        return ResponseEntity.ok(userService.findUsers());
    }

    // --- (MODIFICAT) Acum returnează UserDetailsDTO în loc de Void ---
    // (Și am lăsat securitatea comentată pentru Pasul 0)
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDetailsDTO> create(@Valid @RequestBody UserDetailsDTO user) {
        // --- (MODIFICAT) Primește DTO-ul creat ---
        UserDetailsDTO createdUserDTO = userService.insert(user); // Acum 'insert' returnează DTO

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUserDTO.getId()) // Folosește ID-ul din DTO
                .toUri();

        // --- (MODIFICAT) Returnează 201 + Location + Body-ul (care conține ID-ul) ---
        return ResponseEntity.created(location).body(createdUserDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsDTO> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDetailsDTO userDetailsDTO) {
        UserDetailsDTO updatedUser = userService.updateUser(id, userDetailsDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
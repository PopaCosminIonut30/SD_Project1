package com.example.demo.services;

import com.example.demo.dtos.LoginRequestDTO;
import com.example.demo.dtos.LoginResponseDTO;
import com.example.demo.dtos.RegisterRequestDTO;
import com.example.demo.entities.Credential;
import com.example.demo.repositories.CredentialRepository;
import com.example.demo.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(CredentialRepository credentialRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Înregistrează o credențială nouă (setează parola)
     * Actualizat pentru a salva și rolul.
     */
    public void register(RegisterRequestDTO dto) {
        if (credentialRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        Credential credential = new Credential(
                dto.getUserId(),
                dto.getUsername(),
                hashedPassword,
                dto.getRole()
        );
        credentialRepository.save(credential);
        LOGGER.debug("Credential saved for user {}", dto.getUsername());
    }

    /**
     * Loghează un user și returnează un token JWT
     */
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Credential credential = credentialRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), credential.getPassword())) {
            LOGGER.warn("Failed login attempt for user {}", dto.getUsername());
            throw new RuntimeException("Invalid username or password");
        }


        String token = jwtTokenProvider.generateToken(
                credential.getUsername(),
                credential.getRole(),
                credential.getUserId()
        );
        LOGGER.debug("Token generated for user {}", dto.getUsername());

        return new LoginResponseDTO(token);
    }
}
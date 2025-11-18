package com.example.demo.config;

import com.example.demo.security.AuthEntryPointJwt; // <-- Import NOU
import com.example.demo.security.CustomAccessDeniedHandler; // <-- Import NOU
import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // --- (NOU) Injectăm handler-ele de eroare ---
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    // --- (SFÂRȘIT NOU) ---

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // --- (NOU) Adăugăm managementul excepțiilor ---
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler) // Handler pentru 401
                        .accessDeniedHandler(accessDeniedHandler)     // Handler pentru 403
                )
                // --- (SFÂRȘIT NOU) ---
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
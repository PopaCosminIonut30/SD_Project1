package com.example.demo.dto;

import java.io.Serializable;

public record NotificationDTO(String deviceId, String message, long timestamp) implements Serializable {}
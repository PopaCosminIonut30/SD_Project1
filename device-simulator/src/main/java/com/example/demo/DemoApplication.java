package com.example.demo;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.MeasurementDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner { // Numele clasei: DemoApplication

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${simulator.device-id}")
	private String deviceIdString;

	@Value("${simulator.csv-path}")
	private String csvPath;

	@Value("${simulator.delay}")
	private long delay;

	public static void main(String[] args) {
		// CORECTAT: Pornim DemoApplication.class, nu DeviceSimulatorApplication.class
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Logica rămâne aceeași...
		UUID deviceId;
		try {
			deviceId = UUID.fromString(deviceIdString);
		} catch (IllegalArgumentException e) {
			System.err.println("Invalid UUID format for device-id: " + deviceIdString);
			return;
		}

		System.out.println("Starting simulator for Device ID: " + deviceId);

		// Citirea din CSV într-o buclă infinită (opțional, ca să simuleze continuu)
		// Sau o singură trecere prin fișier. Aici facem o singură trecere.
		try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					double value = Double.parseDouble(line.trim());

					MeasurementDTO measurement = new MeasurementDTO(
							System.currentTimeMillis(),
							deviceId,
							value
					);

					rabbitTemplate.convertAndSend(RabbitMQConfig.DATA_QUEUE, measurement);

					System.out.println("Sent: " + measurement);

					Thread.sleep(delay);

				} catch (NumberFormatException e) {
					System.err.println("Skipping invalid line: " + line);
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading CSV file: " + e.getMessage());
			System.err.println("Make sure 'sensor.csv' exists in the root folder.");
		}

		System.out.println("Simulation finished.");
	}
}
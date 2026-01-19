import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import com.example.demo.dtos.HourlyConsumptionDTO;
import com.example.demo.repositories.SensorDataRepository;
import com.example.demo.entities.SensorData;

@Service
public class MonitoringService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    public List<HourlyConsumptionDTO> getHourlyConsumption(String deviceId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        // Luăm toate măsurătorile pentru device-ul respectiv din ziua aleasă
        List<SensorData> measurements = sensorDataRepository.findByDeviceId(UUID.fromString(deviceId));

        // Map pentru a grupa consumul pe ore (0-23)
        Map<Integer, Double> hourlyMap = new HashMap<>();
        for (int i = 0; i < 24; i++) hourlyMap.put(i, 0.0);

        for (SensorData data : measurements) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(data.getTimestamp()), ZoneId.systemDefault());

            if (dateTime.toLocalDate().equals(date)) {
                int hour = dateTime.getHour();
                hourlyMap.put(hour, hourlyMap.get(hour) + data.getMeasurementValue());
            }
        }

        // Convertim map-ul în lista de DTO-uri cerută de Recharts
        return hourlyMap.entrySet().stream()
                .map(e -> new HourlyConsumptionDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(HourlyConsumptionDTO::hour))
                .collect(Collectors.toList());
    }
}
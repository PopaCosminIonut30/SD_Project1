import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.demo.dtos.HourlyConsumptionDTO;

@RestController
@RequestMapping("/monitoring")
@CrossOrigin
public class MonitoringController {

    @Autowired
    private MonitoringService monitoringService;

    @GetMapping("/history/{deviceId}")
    public ResponseEntity<List<HourlyConsumptionDTO>> getHistory(
            @PathVariable String deviceId,
            @RequestParam String date) {
        return ResponseEntity.ok(monitoringService.getHourlyConsumption(deviceId, date));
    }
}
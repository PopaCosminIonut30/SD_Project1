import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.example.demo.dto.NotificationDTO;

@Component
public class NotificationConsumer {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consumeNotification(NotificationDTO notification) {
        // Trimite alerta pe canalul /topic/notifications
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.example.demo.dto.ChatMessageDTO;

@Controller
public class ChatController {
    @Autowired
    private ChatbotService chatbotService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat") // Clientul trimite la /app/chat
    public void handleChatMessage(ChatMessageDTO message) {
        // 1. Trimitem mesajul utilizatorului înapoi (ca să-l vadă în fereastra de chat)
        messagingTemplate.convertAndSend("/topic/chat", message);

        // 2. Procesăm răspunsul chatbot-ului
        String response = chatbotService.getResponse(message.content());
        ChatMessageDTO botReply = new ChatMessageDTO("Chatbot", response, System.currentTimeMillis());

        // 3. Trimitem răspunsul chatbot-ului către utilizator
        messagingTemplate.convertAndSend("/topic/chat", botReply);
    }
}
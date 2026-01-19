import org.springframework.stereotype.Service;

@Service
public class ChatbotService {
    public String getResponse(String input) {
        String msg = input.toLowerCase();
        if (msg.contains("salut")) return "Salut! Sunt asistentul tău virtual. Cu ce te ajut?";
        if (msg.contains("factura")) return "Facturile sunt disponibile în meniul 'Billing'.";
        if (msg.contains("consum")) return "Poți vedea graficul de consum în tab-ul 'Monitoring'.";
        if (msg.contains("limita")) return "Poți schimba limita de consum din secțiunea 'Devices'.";
        if (msg.contains("eroare")) return "Te rog să ne trimiți un screenshot la support@energy.ro.";
        if (msg.contains("pret")) return "Prețul actual este de 0.8 RON pe kWh.";
        if (msg.contains("dispozitiv")) return "Adăugarea dispozitivelor se face prin butonul 'Add Device'.";
        if (msg.contains("parola")) return "Accesează 'Settings' pentru a-ți schimba parola.";
        if (msg.contains("admin")) return "Un administrator va prelua cererea ta în scurt timp.";
        if (msg.contains("multumesc")) return "Cu plăcere! O zi energetică să ai!";

        return "Nu am înțeles întrebarea. Poți reformula?";
    }
}
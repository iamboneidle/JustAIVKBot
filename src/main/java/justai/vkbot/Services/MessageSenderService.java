package justai.vkbot.Services;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class MessageSenderService {
    @Value("${vk.access.token}")
    private String accessToken;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderService.class);
    public ResponseEntity<String> reflectMessage(Map<String, Object> request) {
        Map<String, Object> object = (Map<String, Object>) request.get("object");
        Map<String, Object> message = (Map<String, Object>) object.get("message");
        Integer userId = (Integer) message.get("from_id");
        String messageText = (String) message.get("text");
        LOGGER.info("New message from user {}: {}", userId, messageText);
        sendMessage(userId, "Вы сказали: " + messageText);

        return ResponseEntity.ok("ok");
    }

    private void sendMessage(Integer userId, String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            String url = "https://api.vk.com/method/messages.send" +
                    "?peer_id=" + userId +
                    "&message=" + encodedMessage +
                    "&random_id=0" +
                    "&access_token=" + accessToken +
                    "&v=5.199";

            LOGGER.info("Sending message to user {}: {}", userId, message);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                client.execute(request);
                LOGGER.info("Message sent");
            } catch (IOException e) {
                LOGGER.error("Error sending message", e);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error encoding message", e);
        }
    }
}

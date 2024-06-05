package justai.vkbot.Controllers;


import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/bot")
public class VKController {
    @Value("${vk.confirmation.token}")
    private String confirmationToken;
    @Value("${vk.access.token}")
    private String accessToken;
    private static final Logger logger = LoggerFactory.getLogger(VKController.class);

    @PostMapping("/callback")
    public ResponseEntity<String> handleVkCallback(@RequestBody Map<String, Object> request) {
        logger.info("Received request: {}", request);

        String type = (String) request.get("type");

        if ("confirmation".equals(type)) {
            logger.info("Confirmation token received");
            return ResponseEntity.ok(confirmationToken);
        } else if ("message_new".equals(type)) {
            Map<String, Object> object = (Map<String, Object>) request.get("object");
            Map<String, Object> message = (Map<String, Object>) object.get("message");
            Integer userId = (Integer) message.get("from_id");
            String messageText = (String) message.get("text");

            logger.info("New message from user {}: {}", userId, messageText);

            sendMessage(userId, "Вы сказали: " + messageText);

            return ResponseEntity.ok("ok");
        }

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

            logger.info("Sending message to user {}: {}", userId, message);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                client.execute(request);
                logger.info("Message sent");
            } catch (IOException e) {
                logger.error("Error sending message", e);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("Error encoding message", e);
        }
    }
}

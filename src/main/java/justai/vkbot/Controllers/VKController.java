package justai.vkbot.Controllers;


import justai.vkbot.Services.MessageSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/bot")
public class VKController extends AbstractVKController{
    @Value("${vk.confirmation.token}")
    private String confirmationToken;
    @Autowired
    private MessageSenderService messageSenderService;
    private static final Logger LOGGER = LoggerFactory.getLogger(VKController.class);

    @PostMapping("/callback")
    public ResponseEntity<String> handleVkCallback(@RequestBody Map<String, Object> request) {
        LOGGER.info("Received request: {}", request);
        String type = (String) request.get("type");
        if ("confirmation".equals(type)) {
            LOGGER.info("Confirmation token received");
            return ResponseEntity.ok(confirmationToken);
        } else if ("message_new".equals(type)) {
            return messageSenderService.reflectMessage(request);
        }
        return ResponseEntity.ok("ok");
    }
}

package com.michiki.michiki.plan.socket;

import com.michiki.michiki.plan.dto.ChatMessage;
import com.michiki.michiki.plan.dto.MousePosition;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class SocketController {

    private final SimpMessagingTemplate template;

    @MessageMapping("/plan/{planId}/mouse")
    public void broadcastMousePosition(@DestinationVariable Long planId, MousePosition mp) {
        template.convertAndSend("/topic/plan/" + planId + "/mouse", mp);
    }

    @MessageMapping("/plan/{planId}/chat")
    public void broadcastChatMessage(@DestinationVariable Long planId, ChatMessage cm){
        template.convertAndSend("/topic/plan/" + planId + "/message", cm);
    }
}

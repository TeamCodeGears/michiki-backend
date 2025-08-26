package com.michiki.michiki.plan.socket;

import com.michiki.michiki.common.auth.dto.StompMemberPrincipal;
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
    public void broadcastMousePosition(@DestinationVariable Long planId, MousePosition mp, Principal principal) {
        if (principal instanceof StompMemberPrincipal user) {
            mp.setMemberId(user.getMemberId());
            mp.setPlanId(planId);
        }
        template.convertAndSend("/topic/plan/" + planId + "/mouse", mp);
    }

    @MessageMapping("/plan/{planId}/message")
    public void broadcastChatMessage(@DestinationVariable Long planId, ChatMessage cm, Principal principal){
        if (principal instanceof StompMemberPrincipal user) {
            cm.setMemberId(user.getMemberId());
            cm.setNickname(user.getNickname());
            cm.setPlanId(planId);
        }
        template.convertAndSend("/topic/plan/" + planId + "/message", cm);
    }
}

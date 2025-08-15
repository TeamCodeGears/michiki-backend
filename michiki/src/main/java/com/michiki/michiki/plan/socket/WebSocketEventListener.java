package com.michiki.michiki.plan.socket;

import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.plan.dto.OnlineMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUserTracker onlineUserTracker;

    // sessionId를 key로 OnlineMemberDto를 저장하는 맵 (OnlineMemberDto에 planId 포함)
    private final Map<String, OnlineMemberDto> sessionInfoMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleSessionConnected(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        String destination = sha.getDestination();
        if (destination != null && destination.matches("/topic/plan/\\d+/online")) {
            Long planId = extractPlanId(destination);
            Principal principal = sha.getUser();
            String sessionId = sha.getSessionId(); // 세션 ID를 가져옵니다.

            if (principal != null && sessionId != null) {
                OnlineMemberDto user = getMemberInfoFromPrincipal(principal);

                // OnlineMemberDto 객체에 planId를 설정합니다.
                // (OnlineMemberDto 클래스에 planId 필드와 Setter가 있다고 가정)
                user.setPlanId(planId);

                // 세션 ID를 키로 사용자 정보를 맵에 저장합니다.
                sessionInfoMap.put(sessionId, user);

                onlineUserTracker.addUser(planId, user);
                broadcastOnlineUsers(planId);
            }
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();

        // 세션 ID로 저장된 사용자 정보를 맵에서 제거하고 가져옵니다.
        OnlineMemberDto onlineMemberDto = sessionInfoMap.remove(sessionId);

        // 세션 정보가 존재할 경우에만 제거 로직을 실행합니다.
        if (onlineMemberDto != null) {
            Long planId = onlineMemberDto.getPlanId();

            onlineUserTracker.removeUser(planId, onlineMemberDto);
            broadcastOnlineUsers(planId);
        }
    }

    private void broadcastOnlineUsers(Long planId) {
        Set<OnlineMemberDto> users = onlineUserTracker.getUsers(planId);
        messagingTemplate.convertAndSend("/topic/plan/" + planId + "/online", users);
    }

    private Long extractPlanId(String destination) {
        String[] split = destination.split("/");
        return Long.parseLong(split[3]);
    }

    private OnlineMemberDto getMemberInfoFromPrincipal(Principal principal) {
        // principal 또는 SecurityContext에서 사용자 정보 조회 후 UserInfo 생성
        // 예) memberId, nickname, profileImage 조회
        return new OnlineMemberDto();
    }
}
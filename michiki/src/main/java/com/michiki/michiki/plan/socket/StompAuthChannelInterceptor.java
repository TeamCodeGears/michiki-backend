package com.michiki.michiki.plan.socket;

import com.michiki.michiki.common.auth.JwtTokenProvider;
import com.michiki.michiki.common.auth.dto.StompMemberPrincipal;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String raw = accessor.getFirstNativeHeader("Authorization");
            String token = raw != null ? raw.replaceFirst("(?i)^Bearer\\s+", "") : null;

            if (token != null) {
                Claims claims = jwtTokenProvider.validateToken(token);
                Long memberId = claims.get("memberId", Long.class);
                String email = claims.getSubject();
                String nickname = claims.get("nickname", String.class);
                String profileImage = claims.get("profileImage", String.class);

                accessor.setUser(new StompMemberPrincipal(memberId, email, nickname, profileImage));
            }
        }
        return message;
    }
}

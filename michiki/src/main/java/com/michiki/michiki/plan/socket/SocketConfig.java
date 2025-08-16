package com.michiki.michiki.plan.socket;

import com.michiki.michiki.common.auth.service.CustomHandshakeHandler;
import com.michiki.michiki.common.auth.service.JwtHandshakeInterceptor;
import com.michiki.michiki.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${jwt.secret}")
    private String secretKey;

    private final MemberService memberService;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new CustomHandshakeHandler(memberService))
                .addInterceptors(new JwtHandshakeInterceptor(secretKey))
                .setAllowedOriginPatterns("*").withSockJS();
    }

}
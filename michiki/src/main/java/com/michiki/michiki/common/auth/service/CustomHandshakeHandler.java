package com.michiki.michiki.common.auth.service;

import com.michiki.michiki.common.auth.dto.StompMemberPrincipal;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final MemberService memberService;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email != null) {
            Member member = memberService.findByMember(email);
            return new StompMemberPrincipal(member.getMemberId(), member.getEmail(), member.getNickname(), member.getProfileImage());
        }
        return null;
    }
}

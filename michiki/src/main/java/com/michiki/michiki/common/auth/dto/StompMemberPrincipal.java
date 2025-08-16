package com.michiki.michiki.common.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Getter
@RequiredArgsConstructor
public class StompMemberPrincipal implements Principal {

    private final Long memberId;
    private final String email;
    private final String nickname;
    private final String profileImage;

    @Override
    public String getName() {
        return email;
    }


}

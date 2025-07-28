package com.michiki.michiki.member.controller;

import com.michiki.michiki.common.auth.JwtTokenProvider;
import com.michiki.michiki.common.auth.dto.AccessTokenDto;
import com.michiki.michiki.common.auth.dto.GoogleProfileDto;
import com.michiki.michiki.common.auth.service.GoogleService;
import com.michiki.michiki.common.auth.service.RefreshTokenService;
import com.michiki.michiki.member.dto.RedirectDto;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.service.MemberService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private GoogleService googleService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void googleLogin_Success() {
        RedirectDto redirectDto = new RedirectDto();
        redirectDto.setCode("code");

        when(googleService.getAccessToken(anyString()))
                .thenReturn(new AccessTokenDto("access_token", null, null, null));
        when(googleService.getGoogleProfile(anyString())).thenReturn(new GoogleProfileDto("socialId", "email", null));
        when(memberService.getMemberBySocialId(anyString())).thenReturn(null);
        when(memberService.createOauth(anyString(), anyString(), any())).thenReturn(
                Member.builder().memberId(1L).email("email").build());
        when(jwtTokenProvider.createAccessToken(anyString())).thenReturn("accessToken");
        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");

        ResponseEntity<?> response = memberController.googleLogin(redirectDto);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(1L, body.get("id"));
        assertEquals("accessToken", body.get("accessToken"));
        assertEquals("refreshToken", body.get("refreshToken"));
    }


    @Test
    void logout_Success() {
        String accessToken = "mockAccessToken";
        String authorizationHeader = "Bearer " + accessToken;
        String email = "user@example.com";

        // 테스트를 위해 Claims 객체를 Mocking
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(email);
        when(jwtTokenProvider.validateToken(accessToken)).thenReturn(claims);
        doNothing().when(refreshTokenService).deleteRefreshToken(email);

        ResponseEntity<?> response = memberController.logout(authorizationHeader);

        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("로그아웃 성공", body.get("message"));
        verify(refreshTokenService).deleteRefreshToken(email);
    }

}
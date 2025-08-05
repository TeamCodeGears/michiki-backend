package com.michiki.michiki.common.controller;


import com.michiki.michiki.common.auth.JwtTokenProvider;
import com.michiki.michiki.common.auth.dto.AccessTokenDto;
import com.michiki.michiki.common.auth.dto.GoogleProfileDto;
import com.michiki.michiki.common.auth.service.GoogleService;
import com.michiki.michiki.common.auth.service.RefreshTokenService;
import com.michiki.michiki.member.entity.Member;
import com.michiki.michiki.member.service.MemberService;
import com.michiki.michiki.member.type.SocialType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Clients", description = "스웨거 로그인 테스트용")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientsRestController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleService googleService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/oauth/google/redirect")
    public ResponseEntity<?>  googleRedirect(@RequestParam String code) {
        // accessToken 발급
        AccessTokenDto accessTokenDto = googleService.getAccessToken(code);

        // 사용자정보 얻기
        GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccessToken());

        // nickname fallback (구글에서 name 없으면 email의 앞부분이나 timestamp 사용)
        String nickname = googleProfileDto.getName();
        if (nickname == null || nickname.isBlank()) {
            if(googleProfileDto.getEmail() != null && googleProfileDto.getEmail().contains("@")) {
                nickname = googleProfileDto.getEmail().split("@")[0];
            } else {
                nickname = "user_" + System.currentTimeMillis();
            }
        }

        // 회원가입이 되어 있지 않다면 회원가입
        Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
        if (originalMember == null) {
            originalMember = memberService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE, nickname);
        }

        //회원 가입 되있는 회원이라면 토큰발급
        // 내부 JWT 토큰 발급 (액세스 토큰 + 리프레시 토큰)
        String accessToken = jwtTokenProvider.createAccessToken(originalMember.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(originalMember.getEmail());

        // 리프레시 토큰은 별도의 저장소에 저장해야 합니다 (DB 또는 Redis 등)
        refreshTokenService.saveRefreshToken(originalMember.getEmail(), refreshToken);

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getMemberId());
        loginInfo.put("accessToken", accessToken);
        loginInfo.put("refreshToken", refreshToken);

        log.info("accessToken: " + accessToken + " refreshToken: " + refreshToken);

        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }
}

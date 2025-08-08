package com.michiki.michiki.common.auth.conroller;

import com.michiki.michiki.common.auth.JwtTokenProvider;
import com.michiki.michiki.common.auth.dto.RefreshRequest;
import com.michiki.michiki.common.auth.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth API", description = "인증 관련 API (토큰 발급 및 재발급)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "리프레시 토큰을 이용한 액세스 토큰 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스 토큰 재발급 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    })

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        try {
            Claims claims = jwtTokenProvider.validateToken(refreshToken);
            String email = claims.getSubject();

            if (!refreshTokenService.isValidRefreshToken(email, refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "invalid_refresh_token", "message", "저장소에 없는 또는 만료된 리프레시 토큰입니다."));
            }

            String accessToken = jwtTokenProvider.createAccessToken(email);
            // 필요시 새로운 refreshToken 재발급, 저장소 교체

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "invalid_refresh_token", "message", "리프레시 토큰 검증 실패"));
        }
    }
}

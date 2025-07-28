package com.michiki.michiki.common.auth.service;

import com.michiki.michiki.common.auth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtTokenProvider jwtTokenProvider;

    // 메모리 기반 저장소 (실제 운영에서는 DB 또는 Redis 권장)
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();


    /**
     * 리프레시 토큰 저장
     * @param email 사용자 이메일 (Key)
     * @param refreshToken 리프레시 토큰 (Value)
     */
    public void saveRefreshToken(String email, String refreshToken) {
        refreshTokenStore.put(email, refreshToken);
    }

    /**
     * 저장된 리프레시 토큰 삭제 (로그아웃 등에서 사용 가능)
     * @param email 사용자 이메일
     */
    public void deleteRefreshToken(String email) {
        refreshTokenStore.remove(email);
    }


    /**
     * 리프레시 토큰 유효성 검사
     * @param token - 리프레시 토큰
     * @return 토큰이 유효하면 Claims 반환, 아니면 예외 발생
     */

    public Claims validateRefreshToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public boolean isValidRefreshToken(String email, String refreshToken) {
        try {
            Claims claims = validateRefreshToken(refreshToken);
            // 토큰이 유효하고, 만료되지 않았으면 true 반환
            // 추가로 필요한 검증 있으면 여기서 수행 가능
            return true;
        } catch (ExpiredJwtException e) {
            // 토큰이 만효된 경우
            return false;
        } catch (JwtException | IllegalArgumentException exception) {
            // 토큰이 존재하지 않거나 변조되어 유효하지 않은 경우
            return false;
        }
    }
}

package com.michiki.michiki.common.auth;

import com.michiki.michiki.common.exception.TokenAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtTokenFilter extends GenericFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String path = httpServletRequest.getRequestURI();

        // swagger 관련 경로를 필터링에서 제외
        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/member/google/login") // 필요하다면 추가로 제외
                || path.equals("/oauth/google/redirect")
                || path.equals("/auth/refresh-token")
        ) {
            chain.doFilter(request, response);
            return;
        }


        String token = httpServletRequest.getHeader("Authorization");
        try {
            if (token != null) {
                if (!token.startsWith("Bearer ")) {
                    throw new TokenAuthenticationException("Bearer 형식 아닙니다.");
                }
                String jwtToken = token.substring(7);
                // token 검증 및 claims(payload) 추출
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();
                // Authentication 객체 생성
                User userDetails = new User(claims.getSubject(), "", new ArrayList<>());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch (TokenAuthenticationException e) {
            log.info("TokenAuthenticationException: {}", e.getMessage());
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getOutputStream().write(("{\"message\": \"" + e.getMessage() + "\"}").getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.info("exception: {}", e.getMessage());
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getOutputStream().write(("{\"message\": \"유효하지 않은 토큰입니다.\"}").getBytes(StandardCharsets.UTF_8));
        }

    }
}

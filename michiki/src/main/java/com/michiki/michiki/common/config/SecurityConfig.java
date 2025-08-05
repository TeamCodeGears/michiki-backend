package com.michiki.michiki.common.config;

import com.michiki.michiki.common.auth.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
                // Basic 인증 비활성화
                // Basic 인증은 사용자이름과 비밀번호를 Base64로 인코딩하여 인증값으로 활용
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션방식을 비활성화
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 특정 url 패턴에 대해서는 인증처리(Authentication 객체생성) 재외
                .authorizeHttpRequests(a -> a.requestMatchers(
                        "/member/google/login",
                        "/oauth/google/redirect",
                        "/auth/refresh-token",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-ui/oauth2-redirect.html"
                ).permitAll().anyRequest().authenticated())
                // UsernamePasswordAuthenticationFilter 이 클래스에서 플로그인 인증을 처리
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"
                ,"http://localhost:8080",
                "https://teamcodegears.github.io" ));
        configuration.setAllowedMethods(List.of("*")); // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더값 허용
        configuration.setAllowCredentials(true); // 자격증명허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //모든 url 패턴에 대해서 cors 허용 설정
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

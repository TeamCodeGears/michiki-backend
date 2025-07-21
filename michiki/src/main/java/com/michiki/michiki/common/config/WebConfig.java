package com.michiki.michiki.common.config;

import com.michiki.michiki.common.mdc.MDCInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MDCInterceptor mdcInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mdcInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                "/favicon.ico",     // 파비콘 요청
                "/actuator/**",     // Actuator 엔드포인트 (있다면)
                "/static/**",       // 정적 리소스 (있다면)
                "/css/**",          // CSS 파일
                "/js/**",           // JavaScript 파일
                "/images/**"        // 이미지 파일
        );

    }
}

package com.michiki.michiki.common.mdc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class MDCInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            String uuid = UUID.randomUUID().toString();
            MDC.put("UUID", uuid);
            MDC.put("RequestPath", request.getRequestURI());

            log.info("[REQUEST] [{}] [{}]", request.getMethod(), request.getRequestURI());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            log.info("[RESPONSE] [{}] [{}] -> [{}]", request.getMethod(), request.getRequestURI(), response.getStatus());
        }

        MDC.clear();
    }
}

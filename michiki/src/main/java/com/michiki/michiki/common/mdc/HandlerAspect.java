package com.michiki.michiki.common.mdc;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HandlerAspect {

    @Before("execution(* com.michiki.michiki..controller.*(..))")
    public void addHandlerInfoToMDC(JoinPoint joinPoint) {
        String handlerName = joinPoint.getSignature().getDeclaringTypeName() + "," + joinPoint.getSignature().getName();
        MDC.put("Handler", handlerName);
    }
}

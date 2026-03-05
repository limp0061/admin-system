package com.project.admin_system.common.aop;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private static final String UNKNOWN = "UNKNOWN";
    private static final String MDC_ADMIN_KEY = "adminId";

    @Around("execution(* com.project.admin_system..*Controller.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        HttpServletResponse response = attrs != null ? attrs.getResponse() : null;

        String method = request != null ? request.getMethod() : UNKNOWN;
        String uri = request != null ? request.getRequestURI() : UNKNOWN;

        MDC.put(MDC_ADMIN_KEY, SecurityUtils.getCurrentActorUsername());

        // 맨 앞에서 SSE 제외
        if (uri != null && uri.endsWith("/notifications/subscribe")) {
            return joinPoint.proceed();
        }

        log.info("[START] {} {} ", method, uri);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();

            int status = response != null ? response.getStatus() : 200;
            log.info("[END] {} {} | Status: {} | {}ms",
                    method, uri, status, System.currentTimeMillis() - start);

            return result;
        } catch (BusinessException e) {
            log.warn("[WARN] {} {} | 400 | {}ms | {}", method, uri, System.currentTimeMillis() - start, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[ERROR] {} {} | 500 | {}ms | {}",
                    method, uri, System.currentTimeMillis() - start, e.getMessage());
            throw e;
        }
    }
}
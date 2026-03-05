package com.project.admin_system.common.filter;

import com.project.admin_system.security.utils.NetworkUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCFilter implements Filter {

    private static final String MDC_REQ_KEY = "requestId";
    private static final String MDC_IP_KEY = "clientIp";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            final UUID uuid = UUID.randomUUID();
            MDC.put(MDC_REQ_KEY, uuid.toString().substring(0, 8));
            MDC.put(MDC_IP_KEY, NetworkUtils.getClientIp(httpRequest));

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
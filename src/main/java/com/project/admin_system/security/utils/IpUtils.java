package com.project.admin_system.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.util.StringUtils;

@Slf4j
public class IpUtils {

    public static String extractIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }

    public static String normalize(String ip) {

        if (ip == null) {
            return null;
        }

        if (ip.startsWith("::ffff:")) {
            ip = ip.substring(7);
        }

        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public static boolean isPureIPv6(String ip) {
        return StringUtils.hasText(ip) && ip.contains(":") && !ip.contains(".");
    }

    public static boolean isAllowed(String clientIp, List<String> allowedIps) {
        if (allowedIps == null || allowedIps.isEmpty()) {
            return false;
        }

        for (String allowedIp : allowedIps) {
            try {
                IpAddressMatcher matcher = new IpAddressMatcher(allowedIp);
                if (matcher.matches(clientIp)) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                log.error("잘못된 IP 형식 등록됨: {}", allowedIp);
            }
        }

        return false;
    }
}

package com.project.admin_system.user.application.dto;

import com.project.admin_system.user.domain.User;

public record AdminAuditLog(
        Long id,
        String roleName,
        String ips
) {
    public static AdminAuditLog from(User user) {
        String ips = user.getAllowedIps() != null ? String.join(",", user.getAllowedIps()) : null;
        return new AdminAuditLog(
                user.getId(),
                user.getRole() != null ? user.getRole().getRoleName() : null,
                ips
        );
    }
}
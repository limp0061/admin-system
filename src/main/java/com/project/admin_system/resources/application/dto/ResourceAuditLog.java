package com.project.admin_system.resources.application.dto;

import com.project.admin_system.resources.domain.Method;
import com.project.admin_system.resources.domain.Resource;
import com.project.admin_system.resources.domain.Role;
import java.util.stream.Collectors;

public record ResourceAuditLog(
        Long resourceId,
        String name,
        String urlPattern,
        Method method,
        String roles,
        String description
) {
    public static ResourceAuditLog from(Resource resource) {
        String roles = resource.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.joining(", "));
        return new ResourceAuditLog(
                resource.getId(),
                resource.getName(),
                resource.getUrlPattern(),
                resource.getMethod(),
                roles,
                resource.getDescription()
        );
    }
}
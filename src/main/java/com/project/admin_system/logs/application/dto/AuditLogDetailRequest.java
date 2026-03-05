package com.project.admin_system.logs.application.dto;

public record AuditLogDetailRequest(
        Long targetEntityId,
        String targetEntityName,
        Object data
) {
}
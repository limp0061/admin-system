package com.project.admin_system.logs.audit.application.dto;

public record AuditLogUpdateRequest(
        Long targetEntityId,
        String targetEntityName,
        Object before,
        Object after
) {
}
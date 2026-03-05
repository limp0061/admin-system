package com.project.admin_system.logs.application.dto;

import com.project.admin_system.logs.domain.AuditAction;
import com.project.admin_system.logs.domain.AuditLog;
import com.project.admin_system.logs.domain.AuditTarget;
import java.time.LocalDateTime;

public record AuditLogListResponse(
        Long id,
        String actorUsername,
        AuditAction action,
        AuditTarget targetEntity,
        LocalDateTime createdAt
) {
    public static AuditLogListResponse from(AuditLog auditLog) {
        return new AuditLogListResponse(
                auditLog.getId(),
                auditLog.getActorUsername(),
                auditLog.getAction(),
                auditLog.getTargetEntity(),
                auditLog.getCreatedAt()
        );
    }
}

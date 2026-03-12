package com.project.admin_system.logs.audit.application.dto;

import com.project.admin_system.logs.audit.domain.AuditAction;
import com.project.admin_system.logs.audit.domain.AuditLog;
import com.project.admin_system.logs.audit.domain.AuditTarget;
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

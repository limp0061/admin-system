package com.project.admin_system.logs.audit.application.dto;

import com.project.admin_system.logs.audit.domain.AuditAction;
import java.time.LocalDateTime;
import java.util.List;

public record AuditLogDetailResponse(
        String actorUsername,
        AuditAction action,
        String targetEntity,
        List<AuditLogDetailItem> details,
        LocalDateTime createdAt
) {

}

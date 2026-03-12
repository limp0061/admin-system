package com.project.admin_system.logs.audit.application.dto;

import java.util.Map;

public record AuditLogDetailItem(
        String targetEntityName,
        Map<String, Object> details
) {
}

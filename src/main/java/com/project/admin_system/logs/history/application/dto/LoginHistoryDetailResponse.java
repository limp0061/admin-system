package com.project.admin_system.logs.history.application.dto;

import java.time.LocalDateTime;

public record LoginHistoryDetailResponse(
        String emailId,
        String rawIp,
        String userAgent,
        String deviceName,
        String failureReason,
        LocalDateTime createdAt
) {

}

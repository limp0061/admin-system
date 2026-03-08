package com.project.admin_system.logs.presentation;

import com.project.admin_system.history.domain.PasswordChangeType;
import com.project.admin_system.history.domain.PasswordHistory;
import java.time.LocalDateTime;

public record PasswordHistoryListResponse(
        String emailId,
        String clientIp,
        PasswordChangeType changeType,
        LocalDateTime createdAt
) {
    public static PasswordHistoryListResponse from(PasswordHistory passwordHistory) {
        return new PasswordHistoryListResponse(
                passwordHistory.getEmailId(),
                passwordHistory.getClientIp(),
                passwordHistory.getChangeType(),
                passwordHistory.getCreatedAt()
        );
    }
}
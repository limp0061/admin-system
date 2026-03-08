package com.project.admin_system.logs.presentation;

import com.project.admin_system.history.domain.LoginHistory;
import com.project.admin_system.user.domain.LoginStatus;
import java.time.LocalDateTime;

public record LoginHistoryListResponse(
        String emailId,
        String clientIp,
        LoginStatus loginStatus,
        String failureReason,
        LocalDateTime createdAt
) {
    public static LoginHistoryListResponse from(LoginHistory loginHistory) {
        return new LoginHistoryListResponse(
                loginHistory.getEmailId(),
                loginHistory.getClientIp(),
                loginHistory.getStatus(),
                loginHistory.getFailureReason(),
                loginHistory.getCreatedAt()
        );
    }
}

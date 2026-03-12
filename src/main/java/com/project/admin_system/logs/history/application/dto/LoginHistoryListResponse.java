package com.project.admin_system.logs.history.application.dto;

import com.project.admin_system.logs.history.domain.DeviceType;
import com.project.admin_system.logs.history.domain.LoginHistory;
import com.project.admin_system.user.domain.LoginStatus;
import java.time.LocalDateTime;

public record LoginHistoryListResponse(
        Long id,
        String emailId,
        String clientIp,
        String os,
        String browser,
        DeviceType deviceType,
        LoginStatus loginStatus,
        LocalDateTime createdAt
) {
    public static LoginHistoryListResponse from(LoginHistory loginHistory) {
        return new LoginHistoryListResponse(
                loginHistory.getId(),
                loginHistory.getEmailId(),
                loginHistory.getClientIp(),
                loginHistory.getOs(),
                loginHistory.getBrowser(),
                loginHistory.getDeviceType(),
                loginHistory.getStatus(),
                loginHistory.getCreatedAt()
        );
    }
}

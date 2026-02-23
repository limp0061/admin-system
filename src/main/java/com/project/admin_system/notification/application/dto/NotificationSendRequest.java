package com.project.admin_system.notification.application.dto;

public record NotificationSendRequest(
        NotificationType type,
        Object data,
        Long noticeId,
        Long userId,
        Boolean isForce
) {
}
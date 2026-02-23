package com.project.admin_system.notification.application.dto;

public record NotificationListResponse(
        Long notificationId,
        String title,
        String url
) {
}

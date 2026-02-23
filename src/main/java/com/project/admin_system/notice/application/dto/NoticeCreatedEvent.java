package com.project.admin_system.notice.application.dto;


public record NoticeCreatedEvent(
        Long id,
        String title,
        boolean isRealTimeNoticed,
        boolean isForce
) {
}

package com.project.admin_system.notice.application.dto;

import com.project.admin_system.notice.domain.Notice;
import java.time.format.DateTimeFormatter;

public record NoticeAuditLog(
        Long id,
        String type,
        String title,
        boolean isRealtimeNotified,
        boolean isForce,
        String startAt,
        String endAt
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static NoticeAuditLog from(Notice notice) {
        return new NoticeAuditLog(
                notice.getId(),
                notice.getType().getLabel(),
                notice.getTitle(),
                notice.isRealtimeNotified(),
                notice.isForce(),
                notice.getStartAt() != null ? notice.getStartAt().format(FORMATTER) : null,
                notice.getEndAt() != null ? notice.getEndAt().format(FORMATTER) : null
        );
    }
}
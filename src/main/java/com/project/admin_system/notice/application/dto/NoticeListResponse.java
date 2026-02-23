package com.project.admin_system.notice.application.dto;

import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notice.domain.NoticeType;

import java.time.LocalDateTime;

public record NoticeListResponse(
        Long id,
        NoticeType type,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime updatedAt
) {
    public static NoticeListResponse from(Notice notice) {
        return new NoticeListResponse(
                notice.getId(),
                notice.getType(),
                notice.getTitle(),
                notice.getStartAt(),
                notice.getEndAt(),
                notice.getUpdatedAt()
        );
    }
}

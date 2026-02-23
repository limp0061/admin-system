package com.project.admin_system.notice.application.dto;

import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notice.domain.NoticeType;
import java.time.LocalDateTime;

public record NoticeDetailResponse(
        Long id,
        NoticeType type,
        String title,
        String content,
        boolean isRealTimeNoticed,
        boolean isForce,

        LocalDateTime startAt,
        LocalDateTime endAt
) {

    public static NoticeDetailResponse from(Notice notice) {
        return new NoticeDetailResponse(
                notice.getId(),
                notice.getType(),
                notice.getTitle(),
                notice.getContent(),
                notice.isRealtimeNotified(),
                notice.isForce(),
                notice.getStartAt(),
                notice.getEndAt()
        );
    }

    public static NoticeDetailResponse empty() {
        return new NoticeDetailResponse(null, null, "", "", false, false, null, null);
    }
}

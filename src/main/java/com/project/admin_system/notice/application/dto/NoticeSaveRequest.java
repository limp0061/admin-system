package com.project.admin_system.notice.application.dto;

import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notice.domain.NoticeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record NoticeSaveRequest(
        Long id,

        @NotNull(message = "유형: 필수 정보입니다.")
        NoticeType type,

        @NotBlank(message = "제목: 필수 정보입니다.")
        String title,
        boolean isRealTimeNoticed,

        boolean isForce,

        @NotBlank(message = "내용: 필수 정보입니다.")
        String content,
        LocalDateTime startAt,
        LocalDateTime endAt
) {

    public NoticeSaveRequest {
        if (startAt != null) {
            startAt = startAt.truncatedTo(ChronoUnit.SECONDS);
        }
        if (endAt != null) {
            endAt = endAt.truncatedTo(ChronoUnit.SECONDS);
        }
    }

    public Notice toEntity() {
        LocalDateTime finalStartAt = (isForce || isRealTimeNoticed || startAt == null)
                ? LocalDateTime.now()
                : startAt;
        boolean finalRealTimeNoticed = isForce || isRealTimeNoticed;

        return Notice.builder()
                .id(id)
                .type(type)
                .title(title)
                .isRealtimeNotified(finalRealTimeNoticed)
                .isForce(isForce)
                .content(content)
                .startAt(finalStartAt)
                .endAt(endAt)
                .build();
    }
}


package com.project.admin_system.notification.application.dto;

import com.project.admin_system.notice.domain.Notice;
import java.time.LocalDateTime;

public record EventData(
        Long notificationId,
        String title,
        LocalDateTime createdAt,
        String url,
        Long userCount,
        Boolean isRead
) {
    public EventData(Long notificationId, String title, LocalDateTime createdAt, String url, Long userCount,
                     Boolean isRead) {
        this.notificationId = notificationId;
        this.title = title;
        this.createdAt = createdAt;
        this.url = url;
        this.userCount = userCount;
        this.isRead = isRead;
    }

    public static EventData empty() {
        return new EventData(0L, null, null, null, 0L, false);
    }

    public static EventData from(Notice notice) {
        return new EventData(
                null,
                notice.getTitle(),
                notice.getCreatedAt(),
                "/notices/" + notice.getId(),
                0L,
                null
        );
    }
}

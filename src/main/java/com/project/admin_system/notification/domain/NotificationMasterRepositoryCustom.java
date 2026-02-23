package com.project.admin_system.notification.domain;

import com.project.admin_system.notification.application.dto.EventData;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationMasterRepositoryCustom {
    Long getUnReadCount(Long userId, LocalDateTime lastNoticeCheckAt);

    Long getUnReadCountOnlyForce(Long userId, LocalDateTime lastNoticeCheckAt);

    List<EventData> findNotificationMasterTop10(Long userId, LocalDateTime lastNoticeCheckAt);

    List<EventData> findNotificationMasterOnlyForceTop10(Long userId, LocalDateTime lastNoticeCheckAt);

    List<NotificationMaster> findAllUnreadByUserId(Long userId);
}

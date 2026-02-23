package com.project.admin_system.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long>,
        NotificationReadRepositoryCustom {
    boolean existsByNotificationIdAndUserId(Long notificationId, Long userId);
}

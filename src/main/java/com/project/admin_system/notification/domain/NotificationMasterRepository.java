package com.project.admin_system.notification.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMasterRepository extends JpaRepository<NotificationMaster, Long>,
        NotificationMasterRepositoryCustom {

    Optional<NotificationMaster> findByNoticeId(Long noticeId);
}

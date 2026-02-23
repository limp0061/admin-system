package com.project.admin_system.notice.handler;

import com.project.admin_system.common.service.RedisEventPublisher;
import com.project.admin_system.notice.application.dto.NoticeCreatedEvent;
import com.project.admin_system.notification.application.dto.EventData;
import com.project.admin_system.notification.application.dto.NotificationSendRequest;
import com.project.admin_system.notification.application.dto.NotificationType;
import com.project.admin_system.notification.domain.NotificationMaster;
import com.project.admin_system.notification.domain.NotificationMasterRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component

@RequiredArgsConstructor
public class NoticeEventHandler {

    private final NotificationMasterRepository notificationMasterRepository;
    private final RedisEventPublisher redisEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNoticeCreated(NoticeCreatedEvent event) {

        NotificationMaster master = notificationMasterRepository.save(
                NotificationMaster.builder()
                        .title(event.title())
                        .url("/notices/" + event.id())
                        .noticeId(event.id())
                        .isForce(event.isForce())
                        .build()
        );

        EventData data = new EventData(master.getId(), event.title(), LocalDateTime.now(), master.getUrl(), 0L,
                false);
        NotificationSendRequest request = new NotificationSendRequest(
                NotificationType.NOTICE_CREATED,
                data,
                master.getId(),
                null,
                event.isForce());

        redisEventPublisher.publishNotification(request);
    }
}

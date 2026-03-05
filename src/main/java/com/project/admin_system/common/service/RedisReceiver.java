package com.project.admin_system.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.admin_system.notification.application.dto.NotificationSendRequest;
import com.project.admin_system.notification.application.service.NotificationService;
import com.project.admin_system.resources.application.dto.ResourceRefreshEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component

@RequiredArgsConstructor
public class RedisReceiver {

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public void receiveResourceMessage(String message) {
        try {
            ResourceRefreshEvent event = objectMapper.readValue(message, ResourceRefreshEvent.class);

            eventPublisher.publishEvent(event);

        } catch (Exception e) {
            log.error("[Redis 수신 실패] Resource 메시지 파싱 오류 | message={} | {}", message, e.getMessage());
            eventPublisher.publishEvent(new ResourceRefreshEvent(null, null, "UNKNOWN"));
        }
    }

    public void receiveNoticeMessage(String message) {
        try {
            NotificationSendRequest request = objectMapper.readValue(message, NotificationSendRequest.class);

            notificationService.broadcast(request);
        } catch (Exception e) {
            log.error("[Redis 수신 실패] Notification 메시지 파싱 오류 | message={} | {}", message, e.getMessage());
        }
    }
}

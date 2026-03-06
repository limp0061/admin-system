package com.project.admin_system.common.service;

import com.project.admin_system.notification.application.dto.NotificationSendRequest;
import com.project.admin_system.resources.application.dto.ResourceRefreshEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic resourceChannelTopic;

    private final ChannelTopic notificationChannelTopic;

    public RedisEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("resourceChannelTopic") ChannelTopic resourceChannelTopic,
            @Qualifier("notificationChannelTopic") ChannelTopic notificationChannelTopic) {
        this.redisTemplate = redisTemplate;
        this.resourceChannelTopic = resourceChannelTopic;
        this.notificationChannelTopic = notificationChannelTopic;
    }


    public void refreshResource(ResourceRefreshEvent event) {
        log.info("Redis 리소스 이벤트 발행 | action: {}, targetIds: {}", event.action(), event.targetIds());
        try {
            redisTemplate.convertAndSend(resourceChannelTopic.getTopic(), event);
        } catch (Exception e) {
            log.error("Redis 리소스 이벤트 발행 실패 | event: {}", event, e);
        }
    }

    public void publishNotification(NotificationSendRequest request) {
        log.info("Redis 알림 발행 | noticeId: {}, userId: {}", request.noticeId(), request.userId());
        try {
            redisTemplate.convertAndSend(notificationChannelTopic.getTopic(), request);
        } catch (Exception e) {
            log.error("Redis 알림 발행 실패 | noticeId: {}", request.noticeId(), e);
        }
    }
}

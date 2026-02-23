package com.project.admin_system.common.service;

import com.project.admin_system.notification.application.dto.NotificationSendRequest;
import com.project.admin_system.resources.application.dto.ResourceRefreshEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

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
        redisTemplate.convertAndSend(resourceChannelTopic.getTopic(), event);
    }

    public void publishNotification(NotificationSendRequest request) {
        redisTemplate.convertAndSend(notificationChannelTopic.getTopic(), request);
    }
}

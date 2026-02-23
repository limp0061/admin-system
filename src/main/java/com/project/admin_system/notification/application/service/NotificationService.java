package com.project.admin_system.notification.application.service;

import static com.project.admin_system.common.dto.RedisConstants.USER_CONFIG_PREFIX;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.RedisManager;
import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notice.domain.NoticeRepository;
import com.project.admin_system.notification.application.dto.EventData;
import com.project.admin_system.notification.application.dto.NotificationSendRequest;
import com.project.admin_system.notification.application.dto.NotificationType;
import com.project.admin_system.notification.domain.NotificationMaster;
import com.project.admin_system.notification.domain.NotificationMasterRepository;
import com.project.admin_system.notification.domain.NotificationRead;
import com.project.admin_system.notification.domain.NotificationReadRepository;
import com.project.admin_system.user.application.dto.UserConfigDto;
import com.project.admin_system.user.domain.UserConfig;
import com.project.admin_system.user.domain.UserConfigRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, List<SseEmitter>> sseEmitterMap = new ConcurrentHashMap<>();
    private final UserConfigRepository userConfigRepository;
    private final NoticeRepository noticeRepository;
    private final NotificationMasterRepository notificationMasterRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final RedisManager redisManager;

    public SseEmitter subscribe(Long id) {
        long timeout = 1000L * 60 * 60;

        SseEmitter sseEmitter = new SseEmitter(timeout);
        sseEmitterMap.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>()).add(sseEmitter);

        sseEmitter.onTimeout(sseEmitter::complete);

        sseEmitter.onError((e) -> {
            sseEmitter.complete();
        });

        sseEmitter.onCompletion(() -> {
            removeEmitter(id, sseEmitter);
        });

        NotificationSendRequest connectEvent = new NotificationSendRequest(
                NotificationType.CONNECT,
                getUnReadCount(id),
                null,
                id,
                true
        );
        sendToClient(id, connectEvent);

        return sseEmitter;
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> emitters = sseEmitterMap.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                sseEmitterMap.remove(userId);
            }
        }
    }

    // 개별 발송
    public void sendToClient(Long userId, NotificationSendRequest request) {
        List<SseEmitter> emitters = sseEmitterMap.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        UserConfigDto config = getUserConfig(userId);

        if (request.isForce() || (canReceiveNotice(config))) {
            for (SseEmitter emitter : emitters) {
                sendToEmitter(userId, emitter, request);
            }
        }
    }

    public void broadcast(NotificationSendRequest request) {
        sseEmitterMap.keySet().forEach(userId -> {
            Long currentCount = getUnReadCount(userId);
            EventData originalData = (EventData) request.data();
            EventData personalData = new EventData(
                    originalData.notificationId(),
                    originalData.title(),
                    originalData.createdAt(),
                    originalData.url(),
                    currentCount,
                    originalData.isRead()
            );
            NotificationSendRequest personalRequest = new NotificationSendRequest(
                    request.type(),
                    personalData,
                    request.noticeId(),
                    userId,
                    request.isForce()
            );

            sendToClient(userId, personalRequest);
        });
    }

    private void sendToEmitter(Long id, SseEmitter sseEmitter, NotificationSendRequest request) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name(request.type().name())
                    .data(request.data())
                    .id(String.valueOf(request.noticeId())));
        } catch (IOException e) {
            removeEmitter(id, sseEmitter);
        }
    }

    public Long getUnReadCount(Long userId) {
        UserConfigDto config = getUserConfig(userId);

        Long count;
        if (config.isReceivedNotice()) {
            count = notificationMasterRepository.getUnReadCount(userId, config.lastNoticeCheckAt());
        } else {
            count = notificationMasterRepository.getUnReadCountOnlyForce(userId, config.lastNoticeCheckAt());
        }

        return count != null ? count : 0L;
    }

    private UserConfigDto getUserConfig(Long userId) {

        UserConfigDto config = redisManager.getData(USER_CONFIG_PREFIX + userId, UserConfigDto.class);

        if (config == null) {
            UserConfig userConfig = userConfigRepository.findByUserId(userId);
            config = UserConfigDto.from(userConfig);
            redisManager.setData(USER_CONFIG_PREFIX + userId, config);
        }

        return config;
    }

    private boolean canReceiveNotice(UserConfigDto config) {
        return config == null || config.isReceivedNotice();
    }

    public List<EventData> findNotificationsByType(Long userId, String type) {
        if ("ALARM".equals(type)) {
            UserConfigDto config = getUserConfig(userId);
            if (config.isReceivedNotice()) {
                return notificationMasterRepository.findNotificationMasterTop10(userId, config.lastNoticeCheckAt());
            } else {
                return notificationMasterRepository.findNotificationMasterOnlyForceTop10(userId,
                        config.lastNoticeCheckAt());
            }

        } else {
            List<Notice> notices = noticeRepository.findNoticeTop(10);
            return notices.stream()
                    .map(EventData::from)
                    .toList();
        }
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {

        NotificationMaster master = notificationMasterRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (notificationReadRepository.existsByNotificationIdAndUserId(notificationId, userId)) {
            return;
        }

        NotificationRead read = NotificationRead.builder()
                .notification(master)
                .userId(userId)
                .build();

        notificationReadRepository.save(read);
    }

    public NotificationMaster findByNoticeId(Long noticeId) {
        return notificationMasterRepository.findByNoticeId(noticeId).orElse(null);
    }

    @Transactional
    public void markReadAll(Long userId) {
        UserConfig userConfig = userConfigRepository.findByUserId(userId);
        if (userConfig == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userConfig.updateLastNoticeCheckAt(LocalDateTime.now());
        redisManager.deleteData(USER_CONFIG_PREFIX + userId);
    }
}

package com.project.admin_system.notification.presentation;

import com.project.admin_system.notification.application.dto.EventData;
import com.project.admin_system.notification.application.dto.NotificationSendRequest;
import com.project.admin_system.notification.application.service.NotificationService;
import com.project.admin_system.security.dto.AccountContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationAPIController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal AccountContext accountContext) {
        Long userId = accountContext.getAccountDto().id();
        return notificationService.subscribe(userId);
    }

    @PostMapping("/send")
    public void sendAlarm(
            @RequestBody NotificationSendRequest request) {
        notificationService.sendToClient(request.userId(), request);
    }

    @GetMapping("/unread-count")
    public Long getUnreadCount(@AuthenticationPrincipal AccountContext accountContext) {

        Long userId = accountContext.getAccountDto().id();
        return notificationService.getUnReadCount(userId);
    }

    @GetMapping
    public List<EventData> loadNotifications(
            @AuthenticationPrincipal AccountContext accountContext,
            @RequestParam(name = "type") String type
    ) {
        Long userId = accountContext.getAccountDto().id();
        return notificationService.findNotificationsByType(userId, type);
    }

    @PostMapping("/readAll")
    public void readAll(@AuthenticationPrincipal AccountContext accountContext) {

        Long userId = accountContext.getAccountDto().id();
        notificationService.markReadAll(userId);
    }
}

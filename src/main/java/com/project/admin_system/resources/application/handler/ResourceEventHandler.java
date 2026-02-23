package com.project.admin_system.resources.application.handler;

import com.project.admin_system.resources.application.dto.ResourceRefreshEvent;
import com.project.admin_system.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceEventHandler {

    private final SecurityResourceService securityResourceService;

    @EventListener
    public void handleResourceRefresh(ResourceRefreshEvent event) {
        log.info("[Security Event] 리소스 갱신 시작 - Action: {}, Target: {}, Target IDs: {}",
                event.action(), event.targetType(), event.targetIds());

        try {
            securityResourceService.refreshResource();
            log.info("[Security Event] 리소스 갱신 완료");
        } catch (Exception e) {
            log.error("[Security Event] 리소스 갱신 중 오류 발생", e);
        }
    }

}

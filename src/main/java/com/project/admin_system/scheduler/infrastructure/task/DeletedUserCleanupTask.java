package com.project.admin_system.scheduler.infrastructure.task;

import com.project.admin_system.file.application.service.FileService;
import com.project.admin_system.file.domain.DomainType;
import com.project.admin_system.user.application.service.UserService;
import com.project.admin_system.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletedUserCleanupTask implements SchedulerTask {

    private final UserService userService;

    private final FileService fileService;

    @Override
    public String getName() {
        return "DELETED_USER_CLEANUP_TASK";
    }

    @Override
    public String getDefaultCron() {
        return "0 0 2 * * *";
    }

    @Override
    public String getDefaultDescription() {
        return "삭제된지 1주일 이상된 계정 완전 삭제 작업";
    }

    @Override
    @Transactional
    public void run() {
        log.info("[{}] 시작", getName());
        try {
            LocalDateTime dateTime = LocalDateTime.now().minusDays(7);
            List<User> deletedUsers = userService.findDeletedUsers(dateTime);

            if (deletedUsers.isEmpty()) {
                log.info("[{}] 삭제할 사용자 없음", getName());
                return;
            }

            List<Long> ids = deletedUsers.stream().map(User::getId).toList();
            fileService.softDeleteFiles(ids, DomainType.PROFILE);

            userService.deleteUsers(deletedUsers);

            log.info("[{}] {}명 삭제 완료", getName(), deletedUsers.size());
        } catch (Exception e) {
            log.error("[{}] failed file cleanup | {}", getName(), e.getMessage());
        }
        log.info("[{}] 종료", getName());
    }
}

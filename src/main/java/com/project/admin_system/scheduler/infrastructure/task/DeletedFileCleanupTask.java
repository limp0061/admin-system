package com.project.admin_system.scheduler.infrastructure.task;

import com.project.admin_system.file.application.service.FileService;
import com.project.admin_system.file.domain.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletedFileCleanupTask implements SchedulerTask {

    private final FileService fileService;

    @Override
    public String getName() {
        return "DELETED_FILE_CLEANUP_TASK";
    }

    @Override
    public String getDefaultCron() {
        return "0 0 3 * * *";
    }

    @Override
    public String getDefaultDescription() {
        return "삭제된 S3 파일 완전 삭제 작업";
    }

    @Override
    @Transactional
    public void run() {
        log.info("[{}] 시작", getName());
        try {
            List<File> deletedFiles = fileService.findDeletedFiles();

            fileService.deleteFiles(deletedFiles);

            log.info("[{}] {}개 삭제 완료", getName(), deletedFiles.size());
        } catch (Exception e) {
            log.error("[{}] failed file cleanup | {}", getName(), e.getMessage());
        }
        log.info("[{}] 종료", getName());
    }
}

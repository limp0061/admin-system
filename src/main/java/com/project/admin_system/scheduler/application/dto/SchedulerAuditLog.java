package com.project.admin_system.scheduler.application.dto;

import com.project.admin_system.scheduler.domain.SchedulerJob;
import java.time.format.DateTimeFormatter;

public record SchedulerAuditLog(
        Long id,
        String name,
        String cron,
        String lastRunAt,
        boolean enabled,
        String description
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static SchedulerAuditLog from(SchedulerJob job) {
        return new SchedulerAuditLog(
                job.getId(),
                job.getName(),
                job.getCron(),
                job.getLastRunAt() != null ? job.getLastRunAt().format(FORMATTER) : null,
                job.isEnabled(),
                job.getDescription()
        );
    }
}
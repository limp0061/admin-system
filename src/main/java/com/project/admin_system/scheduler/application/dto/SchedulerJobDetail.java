package com.project.admin_system.scheduler.application.dto;

import com.project.admin_system.scheduler.domain.SchedulerJob;
import java.time.format.DateTimeFormatter;

public record SchedulerJobDetail(
        Long id,
        String name,
        String cron,
        String cronDescription,
        String nextRunAt,
        String lastRunAt,
        boolean enabled,
        String description
) {
    private static final DateTimeFormatter Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static SchedulerJobDetail of(SchedulerJob job, String cronDescription) {
        return new SchedulerJobDetail(
                job.getId(),
                job.getName(),
                job.getCron(),
                cronDescription,
                job.getNextRunAt() != null ? job.getNextRunAt().format(Formatter) : null,
                job.getLastRunAt() != null ? job.getLastRunAt().format(Formatter) : null,
                job.isEnabled(),
                job.getDescription()
        );
    }
}

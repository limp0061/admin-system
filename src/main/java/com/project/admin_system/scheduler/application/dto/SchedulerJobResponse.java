package com.project.admin_system.scheduler.application.dto;


import com.project.admin_system.scheduler.domain.SchedulerJob;
import java.time.LocalDateTime;

public record SchedulerJobResponse(
        Long id,
        String name,
        String cron,
        LocalDateTime nextRunAt,
        LocalDateTime lastRunAt,
        boolean enabled
) {

    public static SchedulerJobResponse of(SchedulerJob job, String cronDescription) {
        return new SchedulerJobResponse(
                job.getId(),
                job.getName(),
                cronDescription,
                job.getNextRunAt(),
                job.getLastRunAt(),
                job.isEnabled()
        );
    }
}

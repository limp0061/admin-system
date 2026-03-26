package com.project.admin_system.scheduler.application.dto;


import com.project.admin_system.common.annotation.ValidCron;
import jakarta.validation.constraints.NotNull;

public record SchedulerJobUpdateRequest(

        @ValidCron
        String cron,
        String description,
        @NotNull(message = "실행 여부를 입력해주세요.")
        boolean enabled
) {
}

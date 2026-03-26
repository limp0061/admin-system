package com.project.admin_system.scheduler.application.dto;

import jakarta.validation.constraints.NotNull;

public record SchedulerRunRequest(
        @NotNull(message = "실행할 스케줄러의 ID는 필수 입니다")
        Long id
) {
}

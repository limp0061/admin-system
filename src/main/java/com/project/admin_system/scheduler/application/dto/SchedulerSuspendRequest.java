package com.project.admin_system.scheduler.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SchedulerSuspendRequest(
        @NotNull(message = "중지할 스케줄러의 ID 목록을 선택해주세요")
        List<Long> ids
) {
}

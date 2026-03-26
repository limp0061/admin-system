package com.project.admin_system.scheduler.application.dto;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.scheduler.domain.SchedulerJob;
import java.util.List;

public enum SchedulerMode {
    EXECUTE, SUSPEND, RESUME;

    public static SchedulerMode from(String mode) {
        try {
            return valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_SCHEDULER_MODE);
        }
    }

    public String toMessage(List<SchedulerJob> jobs) {
        return switch (this) {
            case EXECUTE -> String.format("'%s' 스케줄러를\n실행하시겠습니까?", jobs.get(0).getName());
            case SUSPEND -> String.format("선택한 %d건의 스케줄러를 중지하시겠습니까?", jobs.size());
            case RESUME -> String.format("선택한 %d건의 스케줄러를 활성화하시겠습니까?", jobs.size());
        };
    }
}

package com.project.admin_system.scheduler.application.service;

import com.project.admin_system.common.dto.ModalViewData;
import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.scheduler.application.dto.SchedulerMode;
import com.project.admin_system.scheduler.domain.SchedulerJob;
import com.project.admin_system.scheduler.domain.SchedulerJobRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerValidator {

    private final SchedulerJobRepository jobRepository;

    public ModalViewData validateForConfirm(List<Long> ids, String mode) {
        List<SchedulerJob> jobs = jobRepository.findAllById(ids);
        if (jobs.size() != ids.size()) {
            throw new BusinessException(ErrorCode.SCHEDULER_NOT_FOUND);
        }
        SchedulerMode schedulerMode = SchedulerMode.from(mode);
        String message = schedulerMode.toMessage(jobs);

        return new ModalViewData(message);
    }
}

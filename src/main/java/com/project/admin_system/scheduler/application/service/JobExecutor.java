package com.project.admin_system.scheduler.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.scheduler.domain.SchedulerJob;
import com.project.admin_system.scheduler.domain.SchedulerJobRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExecutor {

    private final SchedulerExecuteService executeService;
    private final SchedulerJobRepository jobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void runSingleJob(Long jobId) {
        SchedulerJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        CronExpression cron = CronExpression.parse(job.getCron());
        if (!isTimeToRun(job, now, cron)) {
            return;
        }

        if (job.isRunning()) {
            log.warn("Job {} is already running, skip", job.getName());
            return;
        }
        job.markAsRunning();

        try {
            executeService.execute(job.getName());
        } finally {
            job.markAsPending();
            job.updateNextRunAt(cron.next(now));
            job.updateLastRunAt(now);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void runSingleJobManual(Long jobId) {
        SchedulerJob job = jobRepository.findByIdWithLock(jobId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULER_NOT_FOUND));

        if (job.isRunning()) {
            log.warn("Job {} is already running, skip", job.getName());
            throw new BusinessException(ErrorCode.SCHEDULER_ALREADY_RUNNING);
        }
        job.markAsRunning();

        try {
            executeService.execute(job.getName());
        } finally {
            LocalDateTime now = LocalDateTime.now();
            CronExpression cron = CronExpression.parse(job.getCron());

            job.markAsPending();
            job.updateLastRunAt(now);
            job.updateNextRunAt(cron.next(now));
        }
    }

    private boolean isTimeToRun(SchedulerJob job, LocalDateTime now, CronExpression cron) {
        LocalDateTime nextRun = job.getNextRunAt();

        if (nextRun == null) {
            LocalDateTime next = cron.next(job.getCreatedAt());
            job.updateNextRunAt(next);
            return false;
        }

        return !nextRun.isAfter(now);
    }
}

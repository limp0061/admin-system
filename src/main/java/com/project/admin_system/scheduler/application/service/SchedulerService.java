package com.project.admin_system.scheduler.application.service;

import static com.project.admin_system.common.utils.CronUtils.toReadable;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.logs.audit.application.dto.AuditLogDetailRequest;
import com.project.admin_system.logs.audit.application.dto.AuditLogUpdateRequest;
import com.project.admin_system.logs.audit.application.service.AuditLogService;
import com.project.admin_system.logs.audit.domain.AuditAction;
import com.project.admin_system.logs.audit.domain.AuditTarget;
import com.project.admin_system.scheduler.application.dto.SchedulerAuditLog;
import com.project.admin_system.scheduler.application.dto.SchedulerJobDetail;
import com.project.admin_system.scheduler.application.dto.SchedulerJobResponse;
import com.project.admin_system.scheduler.application.dto.SchedulerJobUpdateRequest;
import com.project.admin_system.scheduler.application.dto.SchedulerResumeRequest;
import com.project.admin_system.scheduler.application.dto.SchedulerRunRequest;
import com.project.admin_system.scheduler.application.dto.SchedulerSuspendRequest;
import com.project.admin_system.scheduler.domain.SchedulerJob;
import com.project.admin_system.scheduler.domain.SchedulerJobRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchedulerService {

    private final SchedulerJobRepository jobRepository;
    private final JobExecutor jobExecutor;
    private final AuditLogService auditLogService;
    private final SchedulerJobRepository schedulerJobRepository;

    public Page<SchedulerJobResponse> findAllSchedulerJobs(Pageable pageable) {
        return jobRepository.findAll(pageable)
                .map(job -> SchedulerJobResponse.of(job, toReadable(job.getCron())));
    }

    @Transactional
    public void execute(SchedulerRunRequest request) {
        jobExecutor.runSingleJobManual(request.id());

        SchedulerJob job = jobRepository.findById(request.id())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULER_NOT_FOUND));

        AuditLogDetailRequest detailRequest = new AuditLogDetailRequest(job.getId(), job.getName(),
                SchedulerAuditLog.from(job));
        auditLogService.logCreate(AuditTarget.SCHEDULER, AuditAction.EXECUTE, List.of(detailRequest));
    }

    @Transactional
    public void suspend(SchedulerSuspendRequest request) {
        List<SchedulerJob> jobs = jobRepository.findByIdIn(request.ids());
        if (jobs.isEmpty()) {
            throw new BusinessException(ErrorCode.SCHEDULER_NOT_FOUND);
        }

        jobs.forEach(SchedulerJob::disabled);

        List<AuditLogDetailRequest> details = jobs.stream()
                .map(job -> new AuditLogDetailRequest(job.getId(), job.getName(), SchedulerAuditLog.from(job)))
                .toList();

        auditLogService.logCreate(AuditTarget.SCHEDULER, AuditAction.SUSPEND, details);
    }

    @Transactional
    public void resume(SchedulerResumeRequest request) {
        List<SchedulerJob> jobs = jobRepository.findByIdIn(request.ids());
        if (jobs.isEmpty()) {
            throw new BusinessException(ErrorCode.SCHEDULER_NOT_FOUND);
        }

        jobs.forEach(SchedulerJob::enabled);

        List<AuditLogDetailRequest> details = jobs.stream()
                .map(job -> new AuditLogDetailRequest(job.getId(), job.getName(), SchedulerAuditLog.from(job)))
                .toList();

        auditLogService.logCreate(AuditTarget.SCHEDULER, AuditAction.RESUME, details);
    }

    public SchedulerJobDetail getById(Long id) {
        SchedulerJob job = jobRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULER_NOT_FOUND));

        return SchedulerJobDetail.of(job, toReadable(job.getCron()));
    }

    @Transactional
    public void updateScheduler(Long id, SchedulerJobUpdateRequest request) {
        SchedulerJob job = schedulerJobRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULER_NOT_FOUND));

        SchedulerAuditLog before = SchedulerAuditLog.from(job);
        job.update(request.cron(), request.description(), request.enabled());

        SchedulerAuditLog after = SchedulerAuditLog.from(job);

        AuditLogUpdateRequest updateRequest = new AuditLogUpdateRequest(job.getId(), job.getName(), before,
                after);
        auditLogService.logUpdate(AuditTarget.SCHEDULER, List.of(updateRequest));
    }
}

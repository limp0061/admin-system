package com.project.admin_system.scheduler.application.service;

import com.project.admin_system.scheduler.domain.SchedulerJob;
import com.project.admin_system.scheduler.domain.SchedulerJobRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerManagerService {

    private final SchedulerJobRepository jobRepository;
    private final JobExecutor jobExecutor;

    public void runScheduledJobs() {
        List<SchedulerJob> jobs = jobRepository.findJobsToRun(LocalDateTime.now());

        for (SchedulerJob job : jobs) {
            try {
                jobExecutor.runSingleJob(job.getId());
            } catch (Exception e) {
                log.error("[{}] 스케줄러 상태 확인 및 실행 중 오류 발생", job.getName(), e);
            }
        }
    }

}

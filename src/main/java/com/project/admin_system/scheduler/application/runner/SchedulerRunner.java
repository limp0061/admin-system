package com.project.admin_system.scheduler.application.runner;

import com.project.admin_system.scheduler.application.service.SchedulerManagerService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerRunner {

    private final SchedulerManagerService managerService;

    @Scheduled(fixedDelay = 60000)
    @SchedulerLock(name = "schedulerRunner", lockAtMostFor = "55s", lockAtLeastFor = "50s")
    public void run() {
        managerService.runScheduledJobs();
    }
}

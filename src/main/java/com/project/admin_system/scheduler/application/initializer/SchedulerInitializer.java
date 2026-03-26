package com.project.admin_system.scheduler.application.initializer;

import com.project.admin_system.scheduler.domain.SchedulerJob;
import com.project.admin_system.scheduler.domain.SchedulerJobRepository;
import com.project.admin_system.scheduler.infrastructure.task.SchedulerTask;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerInitializer implements ApplicationRunner {

    private final List<SchedulerTask> schedulerTasks;
    private final SchedulerJobRepository jobRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing scheduler...");

        for (SchedulerTask schedulerTask : schedulerTasks) {
            if (!jobRepository.existsByName(schedulerTask.getName())) {
                String defaultCron = schedulerTask.getDefaultCron();
                CronExpression cron = CronExpression.parse(defaultCron);

                SchedulerJob schedulerJob = SchedulerJob.create(
                        schedulerTask.getName(),
                        defaultCron,
                        schedulerTask.getDefaultDescription(),
                        cron.next(LocalDateTime.now())
                );
                jobRepository.save(schedulerJob);
            }
        }
    }
}

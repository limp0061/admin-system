package com.project.admin_system.scheduler.application.service;

import com.project.admin_system.scheduler.domain.SchedulerExecution;
import com.project.admin_system.scheduler.domain.SchedulerExecutionRepository;
import com.project.admin_system.scheduler.infrastructure.task.SchedulerTask;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
public class SchedulerExecuteService {

    private final Map<String, SchedulerTask> taskMap;

    private final SchedulerExecutionRepository executionRepository;

    public SchedulerExecuteService(
            List<SchedulerTask> tasks,
            SchedulerExecutionRepository executionRepository
    ) {
        this.taskMap = tasks.stream()
                .collect(Collectors.toMap(SchedulerTask::getName, Function.identity()));

        this.executionRepository = executionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(String jobName) {

        log.info("[START] Executing job: {}", jobName);
        SchedulerExecution execution = SchedulerExecution.start(jobName);

        try {
            taskMap.get(jobName).run();
            execution.success();
            log.info("[SUCCESS] Job {} completed successfully", jobName);

        } catch (Exception e) {
            log.error("[ERROR] Exception for Executing Job: {}", jobName, e);
            execution.fail(extractErrorToMessage(e));
        } finally {
            executionRepository.save(execution);
            log.info("[END] Executing job: {}", jobName);
        }
    }

    private String extractErrorToMessage(Exception e) {

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(e.getClass().getSimpleName()).append("]");
        sb.append(Optional.ofNullable(e.getMessage()).orElse("no message"));

        StackTraceElement[] stack = e.getStackTrace();
        if (stack.length > 0) {
            sb.append(" | at ").append(stack[0].toString());
        }

        String message = sb.toString();
        return message.length() > 500 ? message.substring(0, 500) : message;
    }
}

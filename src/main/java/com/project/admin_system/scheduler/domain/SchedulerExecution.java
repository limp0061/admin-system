package com.project.admin_system.scheduler.domain;

import com.project.admin_system.common.domain.BaseEntity;
import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulerExecution extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_id")
    private Long id;

    @Column(nullable = false)
    private String jobName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static SchedulerExecution start(String jobName) {
        SchedulerExecution execution = new SchedulerExecution();
        execution.jobName = jobName;
        execution.startedAt = LocalDateTime.now();
        execution.status = ExecutionStatus.RUNNING;
        return execution;
    }

    public void fail(String message) {
        if (status != ExecutionStatus.RUNNING) {
            throw new BusinessException(ErrorCode.SCHEDULER_NOT_RUNNING);
        }
        this.status = ExecutionStatus.FAIL;
        this.message = message;
        this.finishedAt = LocalDateTime.now();
    }

    public void success() {
        if (status != ExecutionStatus.RUNNING) {
            throw new BusinessException(ErrorCode.SCHEDULER_NOT_RUNNING);
        }
        this.status = ExecutionStatus.SUCCESS;
        this.finishedAt = LocalDateTime.now();
    }
}

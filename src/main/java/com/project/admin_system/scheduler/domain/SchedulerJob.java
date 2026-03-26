package com.project.admin_system.scheduler.domain;

import com.project.admin_system.common.domain.BaseEntity;
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
public class SchedulerJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String cron;

    @Column(length = 255, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    private LocalDateTime nextRunAt;
    private LocalDateTime lastRunAt;

    @Column(nullable = false)
    private boolean enabled;

    public void updateNextRunAt(LocalDateTime now) {
        this.nextRunAt = now;
    }

    public void updateLastRunAt(LocalDateTime now) {
        this.lastRunAt = now;
    }

    public static SchedulerJob create(
            String name, String cron, String description, LocalDateTime nextRunAt
    ) {
        SchedulerJob job = new SchedulerJob();
        job.name = name;
        job.cron = cron;
        job.description = description;
        job.status = JobStatus.PENDING;
        job.lastRunAt = null;
        job.nextRunAt = nextRunAt;
        job.enabled = true;
        return job;
    }

    public boolean isRunning() {
        return this.status == JobStatus.RUNNING;
    }

    public void markAsRunning() {
        this.status = JobStatus.RUNNING;
    }

    public void markAsPending() {
        this.status = JobStatus.PENDING;
    }

    public void disabled() {
        this.enabled = false;
    }

    public void enabled() {
        this.enabled = true;
    }

    public void update(String cron, String description, boolean enabled) {
        this.cron = cron;
        this.description = description;
        this.enabled = enabled;
    }
}

package com.project.admin_system.scheduler.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulerExecutionRepository extends JpaRepository<SchedulerExecution, Long> {
}

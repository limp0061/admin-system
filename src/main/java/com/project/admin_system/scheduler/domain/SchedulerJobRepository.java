package com.project.admin_system.scheduler.domain;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SchedulerJobRepository extends JpaRepository<SchedulerJob, Long> {
    List<SchedulerJob> findByEnabledTrue();

    boolean existsByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM SchedulerJob j WHERE j.id = :id")
    Optional<SchedulerJob> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT j FROM SchedulerJob j WHERE j.enabled = true AND  j.nextRunAt <= :now")
    List<SchedulerJob> findJobsToRun(@Param("now") LocalDateTime now);

    List<SchedulerJob> findByIdIn(List<Long> ids);
}

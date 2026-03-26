package com.project.admin_system.scheduler.infrastructure.task;

public interface SchedulerTask {
    String getName();

    String getDefaultCron();

    String getDefaultDescription();

    void run();
}

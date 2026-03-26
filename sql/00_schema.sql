SET NAMES utf8mb4;

CREATE TABLE `user` (
    `user_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `email_id` varchar(100) NOT NULL,
    `password` varchar(100) NOT NULL,
    `name` varchar(50) NOT NULL,
    `user_code` varchar(20),
    `position` varchar(20),
    `gender` varchar(10) COMMENT 'FEMALE, MALE, NONE',
    `user_status` varchar(20) COMMENT 'ACTIVE, BLOCKED, DELETED, INACTIVE, LOCKED',
    `password_fail_count` integer NOT NULL DEFAULT 0,
    `role_id` bigint,
    `last_login_time` timestamp,
    `profile_path` varchar(255),
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(255)
);

CREATE TABLE `dept` (
    `dept_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `dept_code` varchar(50) UNIQUE NOT NULL,
    `dept_name` varchar(50) NOT NULL,
    `depth` integer NOT NULL,
    `is_active` boolean NOT NULL,
    `sort_order` integer NOT NULL,
    `upper_dept_id` bigint,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(255)
);

CREATE TABLE `roles` (
     `role_id` bigint PRIMARY KEY AUTO_INCREMENT,
     `role_key` varchar(50) UNIQUE NOT NULL,
     `role_name` varchar(100) NOT NULL,
     `depth` integer NOT NULL,
     `is_admin` boolean NOT NULL,
     `parent_id` bigint,
     `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
     `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     `deleted_at` timestamp,
     `created_by` varchar(255)
);

CREATE TABLE `login_history` (
     `history_id`     bigint PRIMARY KEY AUTO_INCREMENT,
     `user_id`        bigint,
     `email_id`       varchar(100) NOT NULL,
     `raw_ip`         varchar(45)  NOT NULL,
     `client_ip`      varchar(45),
     `device_type`    varchar(20),
     `device_name`    varchar(50),
     `os`             varchar(50),
     `browser`        varchar(50),
     `user_agent`     text,
     `status`         varchar(20),
     `failure_reason` text,
     `created_at`     timestamp    DEFAULT CURRENT_TIMESTAMP,
     `updated_at`     timestamp    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     `deleted_at`     timestamp    NULL,
     `created_by`     varchar(255)
);


CREATE TABLE `notice` (
    `notice_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `title` varchar(100) NOT NULL,
    `content` longtext NOT NULL,
    `notice_type` varchar(20) NOT NULL COMMENT 'NORMAL, SYSTEM',
    `is_force` boolean NOT NULL,
    `is_realtime_notified` boolean NOT NULL,
    `start_at` timestamp,
    `end_at` timestamp,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(255)
);

CREATE TABLE `file` (
    `file_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `original_name` varchar(100) NOT NULL,
    `stored_file_name` varchar(100) NOT NULL,
    `file_path` varchar(255) NOT NULL,
    `file_size` bigint,
    `content_type` varchar(50) NOT NULL,
    `domain_type` varchar(20) NOT NULL COMMENT 'NOTICE, PROFILE, TEMP',
    `domain_id` bigint,
    `status` varchar(20) NOT NULL COMMENT 'DELETED, STORAGE, TEMP',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(255)
);

CREATE TABLE `resource` (
    `resource_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `resource_name` varchar(100),
    `url_pattern` varchar(255) NOT NULL,
    `method` varchar(10) NOT NULL COMMENT 'ALL, DELETE, GET, POST, PUT',
    `description` varchar(255),
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(255)
);

CREATE TABLE `resource_roles` (
    `resource_id` bigint NOT NULL,
    `role_id` bigint NOT NULL,
    PRIMARY KEY (`resource_id`, `role_id`)
);

CREATE TABLE `notification_master` (
    `notification_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `title` varchar(100) NOT NULL,
    `url` varchar(255),
    `notice_id` bigint,
    `is_force` boolean NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(255)
);

CREATE TABLE `notification_read` (
    `notification_read_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `notification_id` bigint,
    `user_id` bigint NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(255)
);

    CREATE TABLE `user_config` (
    `user_config_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `is_received_notice` boolean NOT NULL,
    `last_notice_check_at` timestamp,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(255),
    UNIQUE KEY `uk_user_id` (`user_id`)
);

CREATE TABLE `user_dept` (
     `user_dept_id` bigint PRIMARY KEY AUTO_INCREMENT,
     `user_id` bigint,
     `dept_id` bigint
);

CREATE TABLE `user_allowed_ips` (
    `user_id` bigint NOT NULL,
    `ip_address` varchar(50)
);

CREATE TABLE `audit_log` (
    `log_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `actor_id` bigint NOT NULL,
    `actor_username` varchar(100) NOT NULL,
    `action` varchar(20) NOT NULL COMMENT 'CREATE, DELETE, EXCEL_DOWNLOAD, UPDATE, VIEW',
    `target_entity` varchar(20) NOT NULL COMMENT 'DEPT, NOTICE, RESOURCE, ROLE, USER, USER_DEPT',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(100)
);

CREATE TABLE `audit_log_detail` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `log_id` bigint NOT NULL,
    `target_entity_id` bigint,
    `target_entity_name`varchar(255),
    `details` text,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `password_history` (
    `history_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `email_id` varchar(100) NOT NULL,
    `client_ip` varchar(100) NOT NULL,
    `password` varchar(100) NOT NULL,
    `change_type` varchar(20) NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp,
    `created_by` varchar(100)
);

CREATE TABLE `scheduler_job` (
    `job_id` bigint PRIMARY KEY AUTO_INCREMENT,
    `name` varchar(100) NOT NULL UNIQUE,
    `cron` varchar(50) NOT NULL,
    `description` varchar(255) NOT NULL,
    `status` varchar(20) NOT NULL COMMENT 'PENDING, RUNNING',
    `next_run_at` timestamp NULL,
    `last_run_at` timestamp NULL,
    `enabled` boolean NOT NULL DEFAULT true,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` timestamp NULL,
    `created_by` varchar(255)
);

CREATE TABLE `scheduler_execution` (
   `execution_id` bigint PRIMARY KEY AUTO_INCREMENT,
   `job_name` varchar(100) NOT NULL,
   `status` varchar(20) NOT NULL COMMENT 'RUNNING, SUCCESS, FAIL',
   `message` text,
   `started_at` timestamp NULL,
   `finished_at` timestamp NULL,
   `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
   `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `deleted_at` timestamp NULL,
   `created_by` varchar(255)
);


CREATE TABLE `shedlock`(
    `name` VARCHAR(64),
    `lock_until` TIMESTAMP(3) NULL,
    `locked_at` TIMESTAMP(3) NULL,
    `locked_by` VARCHAR(255),
    PRIMARY KEY (name)
);

ALTER TABLE `user` ADD FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);

ALTER TABLE `dept` ADD FOREIGN KEY (`upper_dept_id`) REFERENCES `dept` (`dept_id`);

ALTER TABLE `roles` ADD FOREIGN KEY (`parent_id`) REFERENCES `roles` (`role_id`);

ALTER TABLE `resource_roles` ADD FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`);

ALTER TABLE `resource_roles` ADD FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);

ALTER TABLE `notification_read` ADD FOREIGN KEY (`notification_id`) REFERENCES `notification_master` (`notification_id`);

ALTER TABLE `notification_read` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `user_config` ADD CONSTRAINT `fk_user_config_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `user_dept` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)  ON DELETE CASCADE;;

ALTER TABLE `user_dept` ADD FOREIGN KEY (`dept_id`) REFERENCES `dept` (`dept_id`);

ALTER TABLE `user_allowed_ips` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;
ALTER TABLE `audit_log_detail` ADD FOREIGN KEY (`log_id`) REFERENCES `audit_log` (`log_id`);

GRANT ALL PRIVILEGES ON *.* TO 'admin_user'@'%';
FLUSH PRIVILEGES;
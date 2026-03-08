package com.project.admin_system.history.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long>,
        PasswordHistoryRepositoryCustom {
}

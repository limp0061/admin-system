package com.project.admin_system.history.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long>,
        PasswordHistoryRepositoryCustom {
    List<PasswordHistory> findTop3ByUserIdOrderByCreatedAtDesc(Long userId);
}

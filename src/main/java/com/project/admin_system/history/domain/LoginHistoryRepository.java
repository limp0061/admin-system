package com.project.admin_system.history.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long>, LoginHistoryRepositoryCustom {
}

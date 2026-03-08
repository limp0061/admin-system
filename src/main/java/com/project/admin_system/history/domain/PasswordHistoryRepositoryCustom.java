package com.project.admin_system.history.domain;

import com.project.admin_system.logs.application.dto.HistorySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PasswordHistoryRepositoryCustom {
    Page<PasswordHistory> findPasswordHistoryList(Pageable pageable, HistorySearchRequest request);
}

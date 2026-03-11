package com.project.admin_system.logs.history.domain;

import com.project.admin_system.logs.history.application.dto.HistorySearchRequest;
import com.project.admin_system.logs.history.application.dto.LoginHistoryDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoginHistoryRepositoryCustom {
    Page<LoginHistory> findLoginHistoryList(Pageable pageable, HistorySearchRequest request);

    LoginHistoryDetailResponse findLoginHistoryById(Long id);
}

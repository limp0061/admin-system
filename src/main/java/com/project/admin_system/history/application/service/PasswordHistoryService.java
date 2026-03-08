package com.project.admin_system.history.application.service;

import com.project.admin_system.history.domain.PasswordHistory;
import com.project.admin_system.history.domain.PasswordHistoryRepository;
import com.project.admin_system.logs.application.dto.HistorySearchRequest;
import com.project.admin_system.logs.presentation.PasswordHistoryListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordHistoryService {

    private final PasswordHistoryRepository passwordHistoryRepository;

    public void savePasswordHistory(PasswordHistory passwordHistory) {
        passwordHistoryRepository.save(passwordHistory);
    }

    public Page<PasswordHistoryListResponse> findPasswordHistoryList(Pageable pageable, HistorySearchRequest request) {
        Page<PasswordHistory> passwordHistoryList = passwordHistoryRepository.findPasswordHistoryList(pageable,
                request);

        return passwordHistoryList.map(PasswordHistoryListResponse::from);
    }
}

package com.project.admin_system.logs.history.application.service;

import com.project.admin_system.logs.history.application.dto.HistorySearchRequest;
import com.project.admin_system.logs.history.application.dto.LoginHistoryDetailResponse;
import com.project.admin_system.logs.history.application.dto.LoginHistoryListResponse;
import com.project.admin_system.logs.history.domain.LoginHistory;
import com.project.admin_system.logs.history.domain.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;


    @Transactional
    public void saveLoginHistory(LoginHistory loginHistory) {
        loginHistoryRepository.save(loginHistory);
    }

    public Page<LoginHistoryListResponse> findLoginHistoryList(Pageable pageable, HistorySearchRequest request) {
        Page<LoginHistory> loginHistoryList = loginHistoryRepository.findLoginHistoryList(pageable, request);

        return loginHistoryList.map(LoginHistoryListResponse::from);
    }

    public LoginHistoryDetailResponse findLoginHistoryById(Long id) {
        return loginHistoryRepository.findLoginHistoryById(id);
    }
}

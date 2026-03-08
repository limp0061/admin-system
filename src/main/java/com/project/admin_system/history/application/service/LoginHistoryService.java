package com.project.admin_system.history.application.service;

import static com.project.admin_system.common.dto.RedisConstants.USER_CONFIG_PREFIX;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.RedisManager;
import com.project.admin_system.history.domain.LoginHistory;
import com.project.admin_system.history.domain.LoginHistoryRepository;
import com.project.admin_system.logs.application.dto.HistorySearchRequest;
import com.project.admin_system.logs.presentation.LoginHistoryListResponse;
import com.project.admin_system.user.application.dto.UserConfigDto;
import com.project.admin_system.user.domain.LoginStatus;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserConfig;
import com.project.admin_system.user.domain.UserConfigRepository;
import com.project.admin_system.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginHistoryService {

    private final UserRepository userRepository;
    private final UserConfigRepository userConfigRepository;
    private final RedisManager redisManager;

    private final LoginHistoryRepository loginHistoryRepository;

    public void saveLoginHistory(LoginHistory loginHistory) {
        loginHistoryRepository.save(loginHistory);
    }

    @Async
    @Transactional
    public void successLoginHandle(String emailId) {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.loginSuccess();

        UserConfig userConfig = userConfigRepository.findByUserId(user.getId());
        UserConfigDto config = UserConfigDto.from(userConfig);
        redisManager.setData(USER_CONFIG_PREFIX + user.getId(), config);
    }

    @Transactional
    public void handleLoginFailure(String emailId, String clientIp, String userAgent,
                                   ErrorCode errorCode) {
        userRepository.findByEmailId(emailId).ifPresent(user -> {
            user.loginFailure();
            log.warn("Login failed. emailId: {}, failCount: {}", emailId, user.getPasswordFailCount());
        });

        saveLoginHistory(LoginHistory.builder()
                .emailId(emailId != null ? emailId : "UNKNOWN")
                .clientIp(clientIp)
                .userAgent(userAgent)
                .status(LoginStatus.LOGIN_FAIL)
                .failureReason(errorCode.getMessage())
                .build());
    }

    public Page<LoginHistoryListResponse> findLoginHistoryList(Pageable pageable, HistorySearchRequest request) {
        Page<LoginHistory> loginHistoryList = loginHistoryRepository.findLoginHistoryList(pageable, request);

        return loginHistoryList.map(LoginHistoryListResponse::from);
    }
}

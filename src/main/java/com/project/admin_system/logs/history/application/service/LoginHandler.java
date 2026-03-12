package com.project.admin_system.logs.history.application.service;

import static com.project.admin_system.common.dto.RedisConstants.USER_CONFIG_PREFIX;
import static com.project.admin_system.security.utils.IpUtils.normalize;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.RedisManager;
import com.project.admin_system.logs.history.domain.LoginHistory;
import com.project.admin_system.logs.history.parser.UserAgentInfo;
import com.project.admin_system.logs.history.parser.UserAgentParser;
import com.project.admin_system.security.dto.AccountDto;
import com.project.admin_system.user.application.dto.UserConfigDto;
import com.project.admin_system.user.domain.LoginStatus;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserConfig;
import com.project.admin_system.user.domain.UserConfigRepository;
import com.project.admin_system.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginHandler {

    private final UserRepository userRepository;
    private final UserConfigRepository userConfigRepository;
    private final RedisManager redisManager;
    private final UserAgentParser userAgentParser;
    private final LoginHistoryService loginHistoryService;

    @Async
    @Transactional
    public void successLoginHandle(AccountDto userDto, String rawIp, String userAgent) {
        User user = userRepository.findByEmailId(userDto.emailId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.loginSuccess();

        String clientIp = normalize(rawIp);

        UserAgentInfo userAgentInfo = userAgentParser.parse(userAgent);

        loginHistoryService.saveLoginHistory(LoginHistory.builder()
                .userId(userDto.id())
                .emailId(userDto.emailId())
                .rawIp(rawIp)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .os(userAgentInfo.os())
                .browser(userAgentInfo.browser())
                .deviceType(userAgentInfo.deviceType())
                .deviceName(userAgentInfo.deviceName())
                .status(LoginStatus.LOGIN_SUCCESS)
                .build()
        );

        UserConfig userConfig = userConfigRepository.findByUserId(user.getId());
        UserConfigDto config = UserConfigDto.from(userConfig);
        redisManager.setData(USER_CONFIG_PREFIX + user.getId(), config);
    }

    @Async
    @Transactional
    public void handleLoginFailure(String emailId, String rawIp, String userAgent,
                                   ErrorCode errorCode) {
        userRepository.findByEmailId(emailId).ifPresent(user -> {
            user.loginFailure();
            log.warn("Login failed. emailId: {}, failCount: {}", emailId, user.getPasswordFailCount());
        });

        String clientIp = normalize(rawIp);
        UserAgentInfo userAgentInfo = userAgentParser.parse(userAgent);

        loginHistoryService.saveLoginHistory(LoginHistory.builder()
                .emailId(emailId != null ? emailId : "UNKNOWN")
                .rawIp(rawIp)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .os(userAgentInfo.os())
                .browser(userAgentInfo.browser())
                .deviceType(userAgentInfo.deviceType())
                .deviceName(userAgentInfo.deviceName())
                .status(LoginStatus.LOGIN_FAIL)
                .failureReason(errorCode.getMessage())
                .build());
    }
}

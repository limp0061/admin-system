package com.project.admin_system.security.handler;

import static com.project.admin_system.security.utils.IpUtils.extractIp;
import static com.project.admin_system.security.utils.IpUtils.normalize;

import com.project.admin_system.logs.history.application.service.LoginHistoryService;
import com.project.admin_system.logs.history.domain.LoginHistory;
import com.project.admin_system.logs.history.parser.UserAgentInfo;
import com.project.admin_system.logs.history.parser.UserAgentParser;
import com.project.admin_system.security.dto.AccountContext;
import com.project.admin_system.security.dto.AccountDto;
import com.project.admin_system.user.domain.LoginStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final LoginHistoryService loginHistoryService;
    private final UserAgentParser userAgentParser;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String rawIp = extractIp(request);
        if (authentication != null) {
            AccountContext accountContext = (AccountContext) authentication.getPrincipal();
            AccountDto userDto = accountContext.getAccountDto();
            String emailId = userDto.emailId();

            String clientIp = normalize(rawIp);
            String userAgent = request.getHeader("User-Agent");
            UserAgentInfo userAgentInfo = userAgentParser.parse(userAgent);

            log.info("Logout Success. email : {}", emailId);
            loginHistoryService.saveLoginHistory(LoginHistory
                    .builder()
                    .userId(userDto.id())
                    .emailId(userDto.emailId())
                    .rawIp(rawIp)
                    .clientIp(clientIp)
                    .userAgent(userAgent)
                    .os(userAgentInfo.os())
                    .browser(userAgentInfo.browser())
                    .deviceType(userAgentInfo.deviceType())
                    .deviceName(userAgentInfo.deviceName())
                    .status(LoginStatus.LOGOUT_SUCCESS)
                    .build()
            );
        }
        super.setDefaultTargetUrl("/login?logout");
        super.onLogoutSuccess(request, response, authentication);
    }
}

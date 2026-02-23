package com.project.admin_system.security.handler;

import static com.project.admin_system.security.utils.NetworkUtils.getClientIp;

import com.project.admin_system.user.application.service.LoginHistoryService;
import com.project.admin_system.user.domain.LoginHistory;
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

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String clientIp = getClientIp(request);
        if (authentication != null) {
            String emailId = authentication.getName();
            log.info("Logout Success. email : {}", emailId);

            loginHistoryService.saveLoginHistory(LoginHistory
                    .builder()
                    .emailId(emailId)
                    .userAgent(request.getHeader("User-Agent"))
                    .status(LoginStatus.LOGOUT_SUCCESS)
                    .clientIp(clientIp)
                    .build()
            );
            super.setDefaultTargetUrl("/login?logout");
            super.onLogoutSuccess(request, response, authentication);
        }
    }
}

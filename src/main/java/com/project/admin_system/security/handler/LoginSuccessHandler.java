package com.project.admin_system.security.handler;

import static com.project.admin_system.security.utils.NetworkUtils.getClientIp;

import com.project.admin_system.security.dto.AccountContext;
import com.project.admin_system.security.dto.AccountDto;
import com.project.admin_system.user.application.service.LoginHistoryService;
import com.project.admin_system.user.application.service.UserService;
import com.project.admin_system.user.domain.LoginHistory;
import com.project.admin_system.user.domain.LoginStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final LoginHistoryService loginHistoryService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String clientIp = getClientIp(request);
        String username = authentication.getName();
        AccountContext accountContext = (AccountContext) authentication.getPrincipal();
        AccountDto userDto = accountContext.getAccountDto();
        userService.successLoginHandle(userDto.emailId());

        LoginHistory loginHistory = LoginHistory.builder()
                .emailId(userDto.emailId())
                .userAgent(request.getHeader("User-Agent"))
                .status(LoginStatus.LOGIN_SUCCESS)
                .clientIp(clientIp)
                .build();

        loginHistoryService.saveLoginHistory(loginHistory);

        log.info("Login Success: User[{}] from IP[{}]", username, clientIp);
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, "/dashboard");
        }
    }

}

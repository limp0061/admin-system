package com.project.admin_system.security.handler;

import static com.project.admin_system.security.utils.NetworkUtils.getClientIp;

import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.exception.IpAddressRejectedException;
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
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginHistoryService loginHistoryService;
    private final UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String emailId = request.getParameter("username");
        String clientIp = getClientIp(request);

        ErrorCode errorCode = getErrorCode(exception);

        userService.handleLoginFailure(emailId);

        loginHistoryService.saveLoginHistory(LoginHistory.builder()
                .emailId(emailId != null ? emailId : "UNKNOWN")
                .userAgent(request.getHeader("User-Agent"))
                .status(LoginStatus.LOGIN_FAIL)
                .failureReason(errorCode.getMessage())
                .clientIp(clientIp)
                .build());

        log.warn("Login Failed: User[{}] from IP[{}], Reason[{}]", emailId, clientIp, errorCode.getMessage());

        setDefaultFailureUrl("/login?error=true&exception=" + errorCode.name());
        super.onAuthenticationFailure(request, response, exception);
    }

    private ErrorCode getErrorCode(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException) {
            return ErrorCode.INVALID_PASSWORD;
        } else if (exception instanceof AccountExpiredException) {
            return ErrorCode.USER_EXPIRED;
        } else if (exception instanceof CredentialsExpiredException) {
            return ErrorCode.PASSWORD_EXPIRED;
        } else if (exception instanceof DisabledException) {
            return ErrorCode.USER_STATUS_DISABLED;
        } else if (exception instanceof LockedException) {
            return ErrorCode.USER_STATUS_LOCKED;
        } else if (exception instanceof InsufficientAuthenticationException) {
            return ErrorCode.USER_NOT_AUTHENTICATED;
        } else if (exception instanceof IpAddressRejectedException) {
            return ErrorCode.IP_DENIED;
        } else {
            return ErrorCode.INTERNAL_SERVER_ERROR;
        }
    }
}

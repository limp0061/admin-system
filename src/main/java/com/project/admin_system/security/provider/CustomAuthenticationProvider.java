package com.project.admin_system.security.provider;

import static com.project.admin_system.security.utils.NetworkUtils.getClientIp;

import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.exception.IpAddressRejectedException;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.security.dto.AccountContext;
import com.project.admin_system.security.dto.AccountDto;
import com.project.admin_system.user.application.service.LoginHistoryService;
import com.project.admin_system.user.domain.LoginHistory;
import com.project.admin_system.user.domain.LoginStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final LoginHistoryService loginHistoryService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();
        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, accountContext.getPassword())) {
            throw new BadCredentialsException(ErrorCode.INVALID_PASSWORD.getMessage());
        }

        // TODO: 2차 인증
        Role role = accountContext.getAccountDto().role();
        if (!role.isAdmin()) {
            throw new InsufficientAuthenticationException(ErrorCode.USER_NOT_AUTHENTICATED.getMessage());
        }

        AccountDto userDto = accountContext.getAccountDto();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientIp = getClientIp(request);

        List<String> allowedIps = userDto.allowedIps();
        boolean allowAll = allowedIps != null && allowedIps.contains("0.0.0.0");
        if (!allowAll) {
            if (allowedIps == null || allowedIps.isEmpty() || !allowedIps.contains(clientIp)) {
                log.warn("등록되지 않은 IP 입니다 : 유저={}, 접속IP={}, 허용IP={}",
                        userDto.emailId(), clientIp, allowedIps);

                loginHistoryService.saveLoginHistory(LoginHistory.builder()
                        .emailId(userDto.emailId())
                        .userAgent(request.getHeader("User-Agent"))
                        .status(LoginStatus.LOGIN_FAIL)
                        .failureReason(ErrorCode.IP_DENIED.getMessage())
                        .clientIp(clientIp)
                        .build());

                throw new IpAddressRejectedException(ErrorCode.IP_DENIED.getMessage());
            }
        }

        return new UsernamePasswordAuthenticationToken(accountContext, null, accountContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

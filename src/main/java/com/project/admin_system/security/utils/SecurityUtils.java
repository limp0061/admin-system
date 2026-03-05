package com.project.admin_system.security.utils;

import com.project.admin_system.security.dto.AccountContext;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class SecurityUtils {

    private final String UNKNOWN_USER = "UNKNOWN_USER";
    private final Long UNKNOWN_ID = null;

    public String getCurrentActorUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return UNKNOWN_USER;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }

        return UNKNOWN_USER;
    }

    public Long getCurrentActorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return UNKNOWN_ID;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AccountContext accountContext) {
            return accountContext.getAccountDto().id();
        }
        return UNKNOWN_ID;
    }
}

package com.project.admin_system.user.application.dto;

import com.project.admin_system.user.domain.UserStatus;

public record UserSearchRequest(
        UserStatus status,
        String keyword
) {
}

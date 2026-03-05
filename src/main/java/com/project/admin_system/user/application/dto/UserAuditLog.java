package com.project.admin_system.user.application.dto;

import com.project.admin_system.user.domain.User;

public record UserAuditLog(
        Long id,
        String emailId,
        String name,
        String position,
        String userCode,
        String gender,
        String userStatus,
        String roleName
) {
    public static UserAuditLog from(User user) {
        return new UserAuditLog(
                user.getId(),
                user.getEmailId(),
                user.getName(),
                user.getPosition(),
                user.getUserCode(),
                user.getGender().getLabel(),
                user.getUserStatus().getLabel(),
                user.getRole() != null ? user.getRole().getRoleName() : null
        );
    }
}
package com.project.admin_system.security.dto;

import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserStatus;
import java.time.LocalDateTime;
import java.util.List;

public record AccountDto(
        Long id,
        String username,
        String emailId,
        String userCode,
        Long deptId,
        String deptName,
        Role role,
        int pwFailCount,
        LocalDateTime lastLoginTime,
        UserStatus userStatus,
        List<String> allowedIps
) {

    public static AccountDto from(User user) {
        return new AccountDto(
                user.getId(),
                user.getName(),
                user.getEmailId(),
                user.getUserCode(),
                user.getUserDept() != null ? user.getUserDept().getDept().getId() : null,
                user.getUserDept() != null ? user.getUserDept().getDept().getDeptName() : null,
                user.getRole(),
                user.getPasswordFailCount(),
                user.getLastLoginTime(),
                user.getUserStatus(),
                user.getAllowedIps() != null ? List.copyOf(user.getAllowedIps()) : List.of()
        );
    }
}
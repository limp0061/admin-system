package com.project.admin_system.user.application.dto;

import com.project.admin_system.user.domain.User;
import java.util.List;

public record AdminRoleResponse(
        Long id,
        String name,
        String emailId,
        String deptName,
        Long roleId,
        String roleName,
        List<String> allowedIps
) {

    public static AdminRoleResponse from(User user) {
        return new AdminRoleResponse(
                user.getId(),
                user.getName(),
                user.getEmailId(),
                user.getUserDept() != null ? user.getUserDept().getDept().getDeptName() : null,
                user.getRole() != null ? user.getRole().getId() : null,
                user.getRole() != null ? user.getRole().getRoleName() : null,
                user.getAllowedIps() != null ? List.copyOf(user.getAllowedIps()) : List.of()
        );
    }
}

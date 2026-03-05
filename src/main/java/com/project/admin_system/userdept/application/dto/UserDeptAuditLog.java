package com.project.admin_system.userdept.application.dto;

import com.project.admin_system.userdept.domain.UserDept;

public record UserDeptAuditLog(
        Long userId,
        String name,
        Long deptId,
        String deptName
) {
    public static UserDeptAuditLog from(UserDept userDept) {
        return new UserDeptAuditLog(
                userDept.getUserId(),
                userDept.getUser().getName(),
                userDept.getDept() != null ? userDept.getDept().getId() : null,
                userDept.getDept() != null ? userDept.getDept().getDeptName() : null
        );
    }
}
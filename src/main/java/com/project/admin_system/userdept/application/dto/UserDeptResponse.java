package com.project.admin_system.userdept.application.dto;

import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserStatus;

public record UserDeptResponse(
        Long id,
        String emailId,
        String name,
        Long deptId,
        String deptName,
        String userCode,
        String position,
        UserStatus userStatus
) {
    public static UserDeptResponse from(User user) {
        return new UserDeptResponse(
                user.getId(),
                user.getEmailId(),
                user.getName(),
                user.getUserDept() != null ? user.getUserDept().getDept().getId() : null,
                user.getUserDept() != null ? user.getUserDept().getDept().getDeptName() : null,
                user.getUserCode(),
                user.getPosition(),
                user.getUserStatus()
        );
    }
}

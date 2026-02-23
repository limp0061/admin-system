package com.project.admin_system.user.application.dto;

import com.project.admin_system.user.domain.Gender;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserStatus;
import java.time.LocalDateTime;

public record UserListResponse(
        Long id,
        String emailId,
        String name,
        Long deptId,
        String deptName,
        String userCode,
        String position,
        Gender gender,
        UserStatus userStatus,
        String profilePath,
        Long roleId,
        String roleName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {

    public static UserListResponse from(User user, String profilePath) {
        return new UserListResponse(
                user.getId(),
                user.getEmailId(),
                user.getName(),
                user.getUserDept() != null ? user.getUserDept().getDept().getId() : null,
                user.getUserDept() != null ? user.getUserDept().getDept().getDeptName() : null,
                user.getUserCode(),
                user.getPosition(),
                user.getGender(),
                user.getUserStatus(),
                profilePath,
                user.getRole() != null ? user.getRole().getId() : null,
                user.getRole() != null ? user.getRole().getRoleName() : null,
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }

    public static UserListResponse from(User user) {
        return UserListResponse.from(user, null);
    }
}

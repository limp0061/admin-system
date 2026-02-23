package com.project.admin_system.user.application.dto;

import com.project.admin_system.user.domain.User;
import java.time.LocalDateTime;
import java.util.Set;

public record AdminUserListResponse(
        Long id,
        String name,
        String userCode,
        Long roleId,
        String roleName,
        Set<String> allowedIps,
        LocalDateTime updatedAt
) {

    public static AdminUserListResponse from(User user) {
        return new AdminUserListResponse(
                user.getId(),
                user.getName(),
                user.getUserCode(),
                user.getRole() != null ? user.getRole().getId() : null,
                user.getRole() != null ? user.getRole().getRoleName() : null,
                user.getAllowedIps() != null ? java.util.Set.copyOf(user.getAllowedIps()) : java.util.Set.of(),
                user.getUpdatedAt()
        );
    }
}

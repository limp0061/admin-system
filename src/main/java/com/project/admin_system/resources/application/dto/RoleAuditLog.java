package com.project.admin_system.resources.application.dto;

import com.project.admin_system.resources.domain.Role;

public record RoleAuditLog(
        Long id,
        String roleKey,
        String roleName,
        int depth,
        Long parentId,
        Boolean isAdmin
) {
    public static RoleAuditLog from(Role role) {
        return new RoleAuditLog(
                role.getId(),
                role.getRoleKey(),
                role.getRoleName(),
                role.getDepth(),
                role.getParent() != null ? role.getParent().getId() : null,
                role.isAdmin()
        );
    }
}

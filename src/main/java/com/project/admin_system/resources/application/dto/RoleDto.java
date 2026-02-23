package com.project.admin_system.resources.application.dto;

import com.project.admin_system.resources.domain.Role;

public record RoleDto(
        Long id,
        String roleKey,
        String roleName,
        boolean isAdmin
) {

    public static RoleDto from(Role role) {
        return new RoleDto(
                role.getId(),
                role.getRoleKey(),
                role.getRoleName(),
                role.isAdmin()
        );
    }
}

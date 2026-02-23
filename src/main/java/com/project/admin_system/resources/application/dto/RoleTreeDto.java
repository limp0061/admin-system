package com.project.admin_system.resources.application.dto;

import com.project.admin_system.resources.domain.Role;
import java.util.ArrayList;
import java.util.List;

public record RoleTreeDto(
        Long id,
        Long parentId,
        String roleKey,
        String roleName,
        int depth,
        List<RoleTreeDto> children,
        Boolean isAdmin
) {

    public static RoleTreeDto setParentId(Long parentId) {
        return new RoleTreeDto(null, parentId, null, null, 0, null, null);
    }

    public static RoleTreeDto from(Role role) {
        return new RoleTreeDto(
                role.getId(),
                role.getParent() != null ? role.getParent().getId() : null,
                role.getRoleKey(),
                role.getRoleName(),
                role.getDepth(),
                new ArrayList<>(),
                role.isAdmin()
        );
    }
}
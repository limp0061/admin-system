package com.project.admin_system.resources.application.dto;

public record RoleValidResponse(
        boolean isDeletable,
        RoleDto role,
        String message
) {
    public static RoleValidResponse of(boolean deletable, RoleDto roleDto, String message) {
        return new RoleValidResponse(deletable, roleDto, message);
    }
}

package com.project.admin_system.user.application.dto;

public record UserSearchResponse(
        Long id,
        String name,
        String emailId,
        String deptName
) {
}

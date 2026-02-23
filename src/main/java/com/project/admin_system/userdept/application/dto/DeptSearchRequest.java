package com.project.admin_system.userdept.application.dto;

public record DeptSearchRequest(
        String keyword,
        Long deptId
) {
}
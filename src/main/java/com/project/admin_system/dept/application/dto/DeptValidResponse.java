package com.project.admin_system.dept.application.dto;

import com.project.admin_system.dept.domain.Dept;

public record DeptValidResponse(
        boolean isDeletable,
        String message,
        Dept dept
) {
}


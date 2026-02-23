package com.project.admin_system.resources.application.dto;

import com.project.admin_system.resources.domain.Method;
import com.project.admin_system.resources.domain.Role;

public record ResourcesSearchRequest(
        String keyword,
        Role role,
        Method method
) {
}

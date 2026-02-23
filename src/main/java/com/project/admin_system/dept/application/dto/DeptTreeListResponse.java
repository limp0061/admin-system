package com.project.admin_system.dept.application.dto;

import java.util.List;

public record DeptTreeListResponse(
        DeptTreeMode mode,
        List<DeptNode> nodeList
) {
}
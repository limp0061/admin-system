package com.project.admin_system.dept.application.dto;

import com.project.admin_system.dept.domain.Dept;

public record DeptResponse(
        Long id,
        String deptCode,
        String deptName,
        Long upperDeptId,
        int depth,
        int sortOrder,
        boolean isActive
) {
    public static DeptResponse from(Dept dept) {

        Dept upper = dept.getUpperDept();
        return new DeptResponse(
                dept.getId(),
                dept.getDeptCode(),
                dept.getDeptName(),
                upper != null ? upper.getId() : null,
                dept.getDepth(),
                dept.getSortOrder(),
                dept.isActive()
        );
    }
}
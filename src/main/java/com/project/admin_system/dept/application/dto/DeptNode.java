package com.project.admin_system.dept.application.dto;

import com.project.admin_system.dept.domain.Dept;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeptNode {
    private Long id;
    private String deptCode;
    private String deptName;
    private Long upperDeptId;

    private boolean isActive = true;

    private Integer sortOrder;
    private int depth;
    private int count;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private boolean hasChildren;

    private List<DeptNode> children = new ArrayList<>();

    @Builder
    private DeptNode(Long id, String deptCode, String deptName, Long upperDeptId,
                     boolean isActive, Integer sortOrder, int depth, int count,
                     LocalDateTime createdAt, LocalDateTime updatedAt, boolean hasChildren) {
        this.id = id;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.upperDeptId = upperDeptId;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
        this.depth = depth;
        this.count = count;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.updatedAt = (updatedAt != null) ? updatedAt : LocalDateTime.now();
        this.hasChildren = hasChildren;
    }
}
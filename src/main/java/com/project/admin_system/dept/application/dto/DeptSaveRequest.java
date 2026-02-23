package com.project.admin_system.dept.application.dto;

import com.project.admin_system.dept.domain.Dept;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DeptSaveRequest(

        Long id,

        @NotBlank(message = "부서명 입력해주세요.")
        String deptName,

        @NotBlank(message = "부서 코드를 입력해주세요")
        @Pattern(regexp = "^(?!0$).*", message = "부서 코드로 '0'은 사용할 수 없습니다.")
        String deptCode,

        Long upperDeptId,

        Integer sortOrder,

        Boolean isActive

) {
    public Dept toEntity(int calculatedDepth, Dept upperDept) {
        return Dept.builder()
                .deptCode(deptCode)
                .deptName(deptName)
                .upperDept(upperDept)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .isActive(isActive != null ? isActive : true)
                .depth(calculatedDepth)
                .build();
    }
}

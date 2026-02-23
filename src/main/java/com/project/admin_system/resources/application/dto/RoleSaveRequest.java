package com.project.admin_system.resources.application.dto;

import com.project.admin_system.resources.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RoleSaveRequest(
        Long id,

        @NotBlank(message = "권한 키: 필수 정보입니다.")
        @Pattern(regexp = "^ROLE_[A-Z0-9_]+$", message = "권한 키는 ROLE_로 시작해야 하며, 영문 대문자, 숫자, 언더바(_)만 사용할 수 있습니다.")
        String roleKey,

        @NotBlank(message = "권한 명: 필수 정보입니다.")
        String roleName,

        Long parentId,

        boolean isAdmin
) {

    public Role toEntity(Role parentRole) {
        return Role.builder()
                .roleKey(this.roleKey)
                .roleName(this.roleName)
                .parent(parentRole)
                .depth(parentRole != null ? parentRole.getDepth() + 1 : 0)
                .build();
    }
}

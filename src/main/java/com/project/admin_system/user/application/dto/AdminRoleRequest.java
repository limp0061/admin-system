package com.project.admin_system.user.application.dto;


import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AdminRoleRequest(

        @NotNull(message = "필수 정보입니다.")
        Long id,

        @NotNull(message = "권한: 필수 정보입니다.")
        Long roleId,

        List<String> ips
) {
}

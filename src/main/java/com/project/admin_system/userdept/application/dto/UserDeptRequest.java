package com.project.admin_system.userdept.application.dto;

import java.util.List;

public record UserDeptRequest(
        Long targetDeptId,
        List<UserDeptDto> userDepts,
        String mode
) {
}

package com.project.admin_system.user.application.dto;

import java.util.List;

public record UserStatusChangeRequest(
        List<Long> ids,
        String mode
) {
}

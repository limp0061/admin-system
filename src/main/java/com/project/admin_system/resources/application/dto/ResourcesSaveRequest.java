package com.project.admin_system.resources.application.dto;

import com.project.admin_system.resources.domain.Method;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record ResourcesSaveRequest(
        Long id,

        String name,

        @NotBlank(message = "URL 패턴을 입력해주세요")
        @Pattern(regexp = "^/.*", message = "URL 패턴은 반드시 '/'로 시작해야 합니다.")
        String urlPattern,

        @NotNull(message = "Method 선택해주세요")
        Method method,

        List<Long> roleIds,

        String description
) {
}

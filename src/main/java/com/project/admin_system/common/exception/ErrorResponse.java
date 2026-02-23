package com.project.admin_system.common.exception;

import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        int status,
        List<FieldErrorDetail> fieldErrors
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.name(),
                errorCode.getMessage(),
                errorCode.getStatus().value(),
                List.of()
        );
    }
}

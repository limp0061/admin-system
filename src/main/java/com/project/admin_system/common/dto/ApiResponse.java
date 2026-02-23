package com.project.admin_system.common.dto;

public record ApiResponse<T>(
        String message,
        T data
) {
    public ApiResponse(String message) {
        this(message, null);
    }
}

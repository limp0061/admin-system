package com.project.admin_system.notice.application.dto;


public record NoticeSearchRequest(
        String type,
        NoticeFilter filter,
        String keyword
) {
}

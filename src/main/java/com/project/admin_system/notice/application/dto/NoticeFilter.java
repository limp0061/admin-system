package com.project.admin_system.notice.application.dto;

import lombok.Getter;

@Getter
public enum NoticeFilter {
    ALL("전체"),
    ONGOING("진행중"),
    RESERVE("에약"),
    END("종료"),
    ;

    private final String label;

    NoticeFilter(String label) {
        this.label = label;
    }
}

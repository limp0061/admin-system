package com.project.admin_system.user.domain;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("정상"),
    INACTIVE("임시"),
    BLOCKED("중지"),
    LOCKED("잠금"),
    DELETED("삭제");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }
    
}

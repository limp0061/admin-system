package com.project.admin_system.user.domain;

public enum UserStatusMode {
    ACTIVE("정상"),
    INACTIVE("임시"),
    BLOCKED("중지"),
    LOCKED("잠금"),
    DELETED("삭제"),
    UNLOCKED("잠금 해제"),
    APPROVE("승인"),
    REJECT("반려"),
    REMOVE("완전 삭제"),
    RECOVER("복구");

    private final String label;

    UserStatusMode(String label) {
        this.label = label;
    }
}

package com.project.admin_system.logs.history.domain;

import lombok.Getter;

@Getter
public enum PasswordChangeType {
    INITIAL("초기 설정"),
    SELF("본인 변경"),
    ADMIN("관리자 변경"),
    RESET("비밀번호 초기화");

    private final String label;

    PasswordChangeType(String label) {
        this.label = label;
    }
}
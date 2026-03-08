package com.project.admin_system.user.domain;

import lombok.Getter;

@Getter
public enum LoginStatus {
    LOGIN_SUCCESS("로그인 성공"),
    LOGIN_FAIL("로그인 실패"),
    LOGOUT_SUCCESS("로그아웃 성공"),
    LOGOUT_FAIL("로그아웃 실패");

    private final String label;

    LoginStatus(String label) {
        this.label = label;
    }
}

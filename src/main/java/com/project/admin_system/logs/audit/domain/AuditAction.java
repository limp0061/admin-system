package com.project.admin_system.logs.audit.domain;

import lombok.Getter;

@Getter
public enum AuditAction {
    CREATE("추가"),
    UPDATE("수정"),
    DELETE("삭제"),
    VIEW("조회"),
    UPDATE_PASSWORD("비밀번호 변경"),
    EXCEL_DOWNLOAD("엑셀 다운로드");

    private final String label;

    AuditAction(String label) {
        this.label = label;
    }
}
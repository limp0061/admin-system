package com.project.admin_system.logs.audit.domain;

import lombok.Getter;

@Getter
public enum AuditTarget {
    USER("사용자"),
    ADMIN("관리자"),
    DEPT("부서"),
    USER_DEPT("사용자 부서"),
    ROLE("권한"),
    RESOURCE("리소스"),
    NOTICE("공지사항"),
    SCHEDULER("스케줄러");

    private final String label;

    AuditTarget(String label) {
        this.label = label;
    }
}
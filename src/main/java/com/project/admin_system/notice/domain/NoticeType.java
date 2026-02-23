package com.project.admin_system.notice.domain;

import lombok.Getter;

@Getter
public enum NoticeType {
    NORMAL("일반"), SYSTEM("시스템");

    private String label;

    NoticeType(String label) {
        this.label = label;
    }
}

package com.project.admin_system.user.domain;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    NONE("선택 안함");

    private final String label;

    Gender(String label) {
        this.label = label;
    }
}

package com.project.admin_system.file.domain;

import lombok.Getter;

@Getter
public enum DomainType {
    NOTICE("notice"),
    PROFILE("profile"),
    TEMP("temp");

    private String value;

    private DomainType(String value) {
        this.value = value;
    }

    public String resolvePath(Long id, String fileName) {
        if (this == TEMP || id == null) {
            return String.format("%s/%s", this.value, fileName);
        }
        return String.format("%s/%s/%s", this.value, id, fileName);
    }
}

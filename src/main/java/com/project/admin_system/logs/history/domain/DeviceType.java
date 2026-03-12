package com.project.admin_system.logs.history.domain;

import lombok.Getter;

@Getter
public enum DeviceType {
    DESKTOP("데스크톱"),
    TABLET("태블릿"),
    MOBILE("모바일"),
    UNKNOWN("알수없음");

    private final String label;

    DeviceType(String value) {
        this.label = value;
    }

    public static DeviceType getDeviceType(String device) {
        if (device == null) {
            return UNKNOWN;
        }

        return switch (device) {
            case "Desktop" -> DESKTOP;
            case "Phone" -> MOBILE;
            case "Tablet" -> TABLET;
            default -> UNKNOWN;
        };
    }

}

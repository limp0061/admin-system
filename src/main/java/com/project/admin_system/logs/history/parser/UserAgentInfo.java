package com.project.admin_system.logs.history.parser;

import com.project.admin_system.logs.history.domain.DeviceType;

public record UserAgentInfo(
        String os,
        String browser,
        DeviceType deviceType,
        String deviceName
) {
}

package com.project.admin_system.logs.history.parser;

import com.project.admin_system.logs.history.domain.DeviceType;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserAgentParser {

    private final UserAgentAnalyzer uaa;

    public UserAgentParser() {
        this.uaa = UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                .withField(UserAgent.OPERATING_SYSTEM_NAME)
                .withField(UserAgent.AGENT_NAME_VERSION)
                .withField(UserAgent.DEVICE_CLASS)
                .withField(UserAgent.DEVICE_NAME)
                .build();
    }

    public UserAgentInfo parse(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return new UserAgentInfo("Unknown", "Unknown", DeviceType.UNKNOWN, "Unknown");
        }

        UserAgent info = uaa.parse(userAgent);

        return new UserAgentInfo(
                info.getValue(UserAgent.OPERATING_SYSTEM_NAME),
                info.getValue(UserAgent.AGENT_NAME_VERSION),
                DeviceType.getDeviceType(info.getValue(UserAgent.DEVICE_CLASS)),
                info.getValue(UserAgent.DEVICE_NAME)
        );
    }
}

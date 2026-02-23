package com.project.admin_system.user.application.dto;


import com.project.admin_system.user.domain.UserConfig;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public record UserConfigDto(
        Long userId,
        boolean isReceivedNotice,
        LocalDateTime lastNoticeCheckAt) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static UserConfigDto from(UserConfig userConfig) {
        return new UserConfigDto(
                userConfig.getUserId(),
                userConfig.isReceivedNotice(),
                userConfig.getLastNoticeCheckAt()
        );
    }
}

package com.project.admin_system.user.domain;

import com.project.admin_system.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
public class UserConfig extends BaseEntity {

    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isReceivedNotice;

    private LocalDateTime lastNoticeCheckAt;

    protected UserConfig() {
    }

    @Builder
    public UserConfig(User user, boolean isReceivedNotice, LocalDateTime lastNoticeCheckAt) {
        this.user = user;
        this.isReceivedNotice = isReceivedNotice;
        this.lastNoticeCheckAt = lastNoticeCheckAt;
    }

    public void updateLastNoticeCheckAt(LocalDateTime now) {
        this.lastNoticeCheckAt = now;
    }
}

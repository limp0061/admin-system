package com.project.admin_system.user.domain;

import com.project.admin_system.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
public class UserConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_config_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
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

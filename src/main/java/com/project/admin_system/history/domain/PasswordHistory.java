package com.project.admin_system.history.domain;

import com.project.admin_system.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class PasswordHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 100, nullable = false)
    private String emailId;

    @Column(length = 100, nullable = false)
    private String clientIp;

    @Column(length = 100, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PasswordChangeType changeType;

    protected PasswordHistory() {
    }

    @Builder
    public PasswordHistory(Long userId, String emailId, String clientIp, String password,
                           PasswordChangeType changeType) {
        this.userId = userId;
        this.emailId = emailId;
        this.clientIp = clientIp;
        this.password = password;
        this.changeType = changeType;
    }
}
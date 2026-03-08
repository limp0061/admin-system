package com.project.admin_system.history.domain;

import com.project.admin_system.common.domain.BaseEntity;
import com.project.admin_system.user.domain.LoginStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class LoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    private Long userId;

    @Column(length = 100, nullable = false)
    private String emailId;

    @Column(length = 100, nullable = false)
    private String clientIp;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LoginStatus status;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    protected LoginHistory() {
    }

    @Builder
    public LoginHistory(Long userId, String emailId, String clientIp, String userAgent, LoginStatus status,
                        String failureReason) {
        this.userId = userId;
        this.emailId = emailId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.status = status;
        this.failureReason = failureReason;
    }
}

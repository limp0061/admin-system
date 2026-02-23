package com.project.admin_system.user.domain;

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

@Entity
@Getter
public class LoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String emailId;

    @Column(nullable = false)
    private String clientIp;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    private LoginStatus status;

    private String failureReason;

    protected LoginHistory() {
    }

    @Builder
    public LoginHistory(String emailId, String clientIp, String userAgent, LoginStatus status, String failureReason) {
        this.emailId = emailId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.status = status;
        this.failureReason = failureReason;
    }
}

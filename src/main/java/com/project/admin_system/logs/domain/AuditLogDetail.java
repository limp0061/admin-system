package com.project.admin_system.logs.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
public class AuditLogDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetEntityId;

    private String targetEntityName;

    @Column(columnDefinition = "TEXT")
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AuditLog auditLog;

    protected AuditLogDetail() {
    }

    @Builder
    public AuditLogDetail(Long targetEntityId, String targetEntityName, String details) {
        this.targetEntityId = targetEntityId;
        this.targetEntityName = targetEntityName;
        this.details = details;
    }

    public void setAuditLog(AuditLog auditLog) {
        this.auditLog = auditLog;
    }
}
package com.project.admin_system.logs.audit.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.project.admin_system.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class AuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(nullable = false)
    private Long actorId;

    @Column(nullable = false)
    private String actorUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditTarget targetEntity;

    @OneToMany(mappedBy = "auditLog", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuditLogDetail> auditLogDetails = new ArrayList<>();

    protected AuditLog() {
    }

    @Builder
    public AuditLog(Long actorId, String actorUsername, AuditAction action, AuditTarget targetEntity) {
        this.actorId = actorId;
        this.actorUsername = actorUsername;
        this.action = action;
        this.targetEntity = targetEntity;
    }

    public static AuditLog of(
            Long actorId, String actorUsername, AuditAction action, AuditTarget targetEntity
    ) {
        AuditLog log = new AuditLog();
        log.actorId = actorId;
        log.actorUsername = actorUsername;
        log.action = action;
        log.targetEntity = targetEntity;

        return log;
    }

    public void addAuditLogDetail(AuditLogDetail detail) {
        auditLogDetails.add(detail);
        detail.setAuditLog(this);
    }
}
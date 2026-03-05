package com.project.admin_system.logs.domain;

import com.project.admin_system.logs.application.dto.AuditLogListResponse;
import com.project.admin_system.logs.application.dto.AuditLogSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogRepositoryCustom {
    Page<AuditLogListResponse> findAuditLogs(Pageable pageable, AuditLogSearchRequest request);
}
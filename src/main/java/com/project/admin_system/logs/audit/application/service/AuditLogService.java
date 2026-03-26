package com.project.admin_system.logs.audit.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.logs.audit.application.dto.AuditLogDetailItem;
import com.project.admin_system.logs.audit.application.dto.AuditLogDetailRequest;
import com.project.admin_system.logs.audit.application.dto.AuditLogDetailResponse;
import com.project.admin_system.logs.audit.application.dto.AuditLogListResponse;
import com.project.admin_system.logs.audit.application.dto.AuditLogSearchRequest;
import com.project.admin_system.logs.audit.application.dto.AuditLogUpdateRequest;
import com.project.admin_system.logs.audit.domain.AuditAction;
import com.project.admin_system.logs.audit.domain.AuditLog;
import com.project.admin_system.logs.audit.domain.AuditLogDetail;
import com.project.admin_system.logs.audit.domain.AuditLogRepository;
import com.project.admin_system.logs.audit.domain.AuditTarget;
import com.project.admin_system.logs.audit.utils.AuditLogParse;
import com.project.admin_system.security.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;
    private final AuditLogParse auditLogParse;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCreate(AuditTarget targetEntity, List<AuditLogDetailRequest> detailRequests) {
        saveAudit(targetEntity, AuditAction.CREATE, detailRequests);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logCreate(AuditTarget targetEntity, AuditAction auditAction,
                          List<AuditLogDetailRequest> detailRequests) {
        saveAudit(targetEntity, auditAction, detailRequests);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUpdate(AuditTarget targetEntity, List<AuditLogUpdateRequest> updateRequests) {

        List<AuditLogDetailRequest> details = new ArrayList<>();
        for (AuditLogUpdateRequest update : updateRequests) {
            JsonNode beforeNode = objectMapper.valueToTree(update.before());
            JsonNode afterNode = objectMapper.valueToTree(update.after());

            Map<String, Object> changes = new LinkedHashMap<>();
            beforeNode.fieldNames().forEachRemaining(field -> {
                JsonNode oldVal = beforeNode.get(field);
                JsonNode newVal = afterNode.get(field);
                if (!Objects.equals(oldVal, newVal)) {
                    Map<String, Object> changeDetail = new LinkedHashMap<>();
                    changeDetail.put("old", oldVal);
                    changeDetail.put("new", newVal);
                    changes.put(field, changeDetail);
                }
            });
            if (!changes.isEmpty()) {
                AuditLogDetailRequest detail = new AuditLogDetailRequest(update.targetEntityId(),
                        update.targetEntityName(), changes);
                details.add(detail);
            }
        }

        if (!details.isEmpty()) {
            saveAudit(targetEntity, AuditAction.UPDATE, details);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDelete(AuditTarget targetEntity, List<AuditLogDetailRequest> detailRequests) {
        saveAudit(targetEntity, AuditAction.DELETE, detailRequests);
    }


    private void saveAudit(AuditTarget targetEntity, AuditAction action, List<AuditLogDetailRequest> dto) {

        if (dto == null || dto.isEmpty()) {
            log.warn("로그에 상세 내역이 없습니다. (Target: {}, Action: {})", targetEntity, action);
            return;
        }

        String adminUserName = SecurityUtils.getCurrentActorUsername();
        Long adminId = SecurityUtils.getCurrentActorId();

        AuditLog auditLog = AuditLog.of(
                adminId,
                adminUserName,
                action,
                targetEntity
        );

        for (AuditLogDetailRequest detail : dto) {
            Long targetEntityId = detail.targetEntityId();
            String data = toJson(detail.data());

            AuditLogDetail auditLogDetail = AuditLogDetail.builder()
                    .targetEntityId(targetEntityId)
                    .targetEntityName(detail.targetEntityName())
                    .details(data)
                    .build();

            auditLog.addAuditLogDetail(auditLogDetail);
        }

        auditLogRepository.save(auditLog);
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    public Page<AuditLogListResponse> findAuditLogs(Pageable pageable, AuditLogSearchRequest request) {
        return auditLogRepository.findAuditLogs(pageable, request);
    }

    public AuditLogDetailResponse findAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOG_NOT_FOUND));

        AuditTarget auditTarget = auditLog.getTargetEntity();
        List<AuditLogDetail> details = auditLog.getAuditLogDetails();
        List<AuditLogDetailItem> list = details.stream()
                .map(detail -> new AuditLogDetailItem(detail.getTargetEntityName(),
                        auditLogParse.parse(detail.getDetails(), auditTarget)))
                .filter(detail -> !detail.details().isEmpty())
                .toList();

        return new AuditLogDetailResponse(
                auditLog.getActorUsername(),
                auditLog.getAction(),
                auditTarget.getLabel(),
                list,
                auditLog.getCreatedAt()
        );
    }
}
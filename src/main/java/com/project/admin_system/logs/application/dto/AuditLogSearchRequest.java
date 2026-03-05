package com.project.admin_system.logs.application.dto;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.logs.domain.AuditAction;
import com.project.admin_system.logs.domain.AuditTarget;
import java.time.LocalDate;

public record AuditLogSearchRequest(
        AuditAction action,
        AuditTarget targetEntity,
        LocalDate startAt,
        LocalDate endAt
) {

    public AuditLogSearchRequest normalize() {

        if (startAt == null && endAt == null) {
            LocalDate now = LocalDate.now();
            return new AuditLogSearchRequest(
                    action,
                    targetEntity,
                    now.minusMonths(1),
                    now
            );
        }

        if (startAt == null || endAt == null) {
            throw new BusinessException(ErrorCode.DATE_REQUIRED);
        }

        if (startAt.isAfter(endAt)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }

        if (startAt.plusDays(90).isBefore(endAt)) {
            throw new BusinessException(ErrorCode.DATE_RANGE_EXCEEDED);
        }

        return this;
    }
}

package com.project.admin_system.logs.history.application.dto;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import java.time.LocalDate;

public record HistorySearchRequest(
        LocalDate startAt,
        LocalDate endAt,
        String emailId
) {
    public HistorySearchRequest normalize() {

        if (startAt == null && endAt == null) {
            LocalDate now = LocalDate.now();
            return new HistorySearchRequest(
                    now.minusMonths(1),
                    now,
                    emailId
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
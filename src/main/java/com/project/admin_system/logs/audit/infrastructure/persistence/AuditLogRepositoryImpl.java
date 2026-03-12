package com.project.admin_system.logs.audit.infrastructure.persistence;

import static com.project.admin_system.common.utils.QueryDslUtil.getOrderSpecifier;
import static com.project.admin_system.logs.audit.domain.QAuditLog.auditLog;

import com.project.admin_system.logs.audit.application.dto.AuditLogListResponse;
import com.project.admin_system.logs.audit.application.dto.AuditLogSearchRequest;
import com.project.admin_system.logs.audit.domain.AuditAction;
import com.project.admin_system.logs.audit.domain.AuditLogRepositoryCustom;
import com.project.admin_system.logs.audit.domain.AuditTarget;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AuditLogListResponse> findAuditLogs(Pageable pageable, AuditLogSearchRequest request) {

        LocalDateTime startAt = request.startAt().atStartOfDay();
        LocalDateTime endAt = request.endAt().atTime(LocalTime.MAX);

        List<AuditLogListResponse> content = queryFactory.select(
                        Projections.constructor(AuditLogListResponse.class,
                                auditLog.id,
                                auditLog.actorUsername,
                                auditLog.action,
                                auditLog.targetEntity,
                                auditLog.createdAt
                        )
                )
                .from(auditLog)
                .where(
                        betweenDate(startAt, endAt),
                        eqAction(request.action()),
                        eqTargetEntity(request.targetEntity())
                )
                .orderBy(getOrderSpecifier(pageable.getSort(), auditLog))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(auditLog.count())
                .from(auditLog)
                .where(
                        betweenDate(startAt, endAt),
                        eqAction(request.action()),
                        eqTargetEntity(request.targetEntity())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression betweenDate(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null) {
            endAt = LocalDateTime.now();
            startAt = LocalDateTime.now().minusMonths(1);
        }
        return auditLog.createdAt.between(startAt, endAt);
    }

    private BooleanExpression eqAction(AuditAction action) {
        return action == null ? null : auditLog.action.eq(action);
    }

    private BooleanExpression eqTargetEntity(AuditTarget targetEntity) {
        return targetEntity == null ? null : auditLog.targetEntity.eq(targetEntity);
    }
}
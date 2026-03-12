package com.project.admin_system.logs.history.infrastructure.persistence;

import static com.project.admin_system.common.utils.QueryDslUtil.getOrderSpecifier;
import static com.project.admin_system.logs.history.domain.QPasswordHistory.passwordHistory;

import com.project.admin_system.logs.history.application.dto.HistorySearchRequest;
import com.project.admin_system.logs.history.domain.PasswordHistory;
import com.project.admin_system.logs.history.domain.PasswordHistoryRepositoryCustom;
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
public class PasswordHistoryRepositoryImpl implements PasswordHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PasswordHistory> findPasswordHistoryList(Pageable pageable, HistorySearchRequest request) {

        LocalDateTime startAt = request.startAt().atStartOfDay();
        LocalDateTime endAt = request.endAt().atTime(LocalTime.MAX);

        List<PasswordHistory> content = queryFactory.selectFrom(passwordHistory)
                .where(
                        betweenDate(startAt, endAt),
                        containsEmailId(request.emailId())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort(), passwordHistory))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(passwordHistory.count())
                .from(passwordHistory)
                .where(
                        betweenDate(startAt, endAt),
                        containsEmailId(request.emailId()
                        )
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression betweenDate(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null) {
            endAt = LocalDateTime.now();
            startAt = LocalDateTime.now().minusMonths(1);
        }
        return passwordHistory.createdAt.between(startAt, endAt);
    }

    private BooleanExpression containsEmailId(String emailId) {
        return emailId != null ? passwordHistory.emailId.contains(emailId) : null;
    }
}

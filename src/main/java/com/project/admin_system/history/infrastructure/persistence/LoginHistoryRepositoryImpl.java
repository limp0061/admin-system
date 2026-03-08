package com.project.admin_system.history.infrastructure.persistence;

import static com.project.admin_system.common.utils.QueryDslUtil.getOrderSpecifier;
import static com.project.admin_system.history.domain.QLoginHistory.loginHistory;

import com.project.admin_system.history.domain.LoginHistory;
import com.project.admin_system.history.domain.LoginHistoryRepositoryCustom;
import com.project.admin_system.logs.application.dto.HistorySearchRequest;
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
public class LoginHistoryRepositoryImpl implements LoginHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<LoginHistory> findLoginHistoryList(Pageable pageable, HistorySearchRequest request) {

        LocalDateTime startAt = request.startAt().atStartOfDay();
        LocalDateTime endAt = request.endAt().atTime(LocalTime.MAX);

        List<LoginHistory> content = queryFactory.selectFrom(loginHistory)
                .where(
                        betweenDate(startAt, endAt),
                        containsEmailId(request.emailId())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort(), loginHistory))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(loginHistory.count())
                .from(loginHistory)
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
        return loginHistory.createdAt.between(startAt, endAt);
    }

    private BooleanExpression containsEmailId(String emailId) {
        return emailId != null ? loginHistory.emailId.contains(emailId) : null;
    }
}

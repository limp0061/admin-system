package com.project.admin_system.notice.infrastructure.persistence;

import static com.project.admin_system.common.utils.QueryDslUtil.getOrderSpecifier;
import static com.project.admin_system.notice.domain.QNotice.notice;

import com.project.admin_system.notice.application.dto.NoticeFilter;
import com.project.admin_system.notice.application.dto.NoticeListResponse;

import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notice.domain.NoticeRepositoryCustom;
import com.project.admin_system.notice.domain.NoticeType;
import com.project.admin_system.notice.application.dto.NoticeSearchRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NoticeListResponse> findAllNotice(Pageable pageable, NoticeSearchRequest request) {
        List<NoticeListResponse> content = queryFactory.select(
                        Projections.constructor(NoticeListResponse.class,
                                notice.id,
                                notice.type,
                                notice.title,
                                notice.startAt,
                                notice.endAt,
                                notice.updatedAt
                        )
                )
                .from(notice)
                .where(
                        noticeTypeEq(request.type()),
                        noticeFilter(request.filter()),
                        containsTitle(request.keyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort(), notice))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(notice.count())
                .from(notice)
                .where(
                        noticeTypeEq(request.type()),
                        noticeFilter(request.filter()),
                        containsTitle(request.keyword())
                );
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Notice> findNoticeTop(int limitCount) {
        return queryFactory.select(notice)
                .from(notice)
                .orderBy(notice.updatedAt.desc())
                .limit(limitCount)
                .fetch();
    }

    @Override
    public long deleteByIdInBatch(List<Long> ids) {
        return queryFactory.delete(notice)
                .where(notice.id.in(ids))
                .execute();
    }

    private BooleanExpression noticeFilter(NoticeFilter filter) {
        if (filter == null || filter.equals(NoticeFilter.ALL)) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        switch (filter) {
            case ONGOING -> {
                return notice.startAt.loe(now)
                        .and(notice.endAt.goe(now));
            }
            case RESERVE -> {
                return notice.startAt.gt(now);
            }
            case END -> {
                return notice.endAt.lt(now);
            }
            default -> {
                return null;
            }
        }
    }

    private BooleanExpression noticeTypeEq(String type) {
        if (!StringUtils.hasText(type) || "ALL".equals(type)) {
            return null;
        }

        NoticeType noticeType = NoticeType.valueOf(type);
        return notice.type.eq(noticeType);
    }

    private BooleanExpression containsTitle(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return notice.title.contains(keyword);
    }
}

package com.project.admin_system.resources.infrastructure.persistence;

import static com.project.admin_system.common.utils.QueryDslUtil.getOrderSpecifier;
import static com.project.admin_system.resources.domain.QResource.resource;
import static com.project.admin_system.resources.domain.QRole.role;

import com.project.admin_system.resources.application.dto.ResourcesSearchRequest;
import com.project.admin_system.resources.domain.Method;
import com.project.admin_system.resources.domain.Resource;
import com.project.admin_system.resources.domain.ResourcesRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ResourcesRepositoryImpl implements ResourcesRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Resource> findAllOrderBySortOrder(Pageable pageable, ResourcesSearchRequest request) {

        List<Resource> content = queryFactory.selectFrom(resource)
                .where(containsUrlPattern(request.keyword()),
                        eqMethod(request.method())
                )
                .orderBy(getOrderSpecifier(pageable.getSort(), resource))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(resource.count())
                .from(resource)
                .where(containsUrlPattern(request.keyword()),
                        eqMethod(request.method())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Resource> findAllWithRoles() {
        return queryFactory
                .selectFrom(resource)
                .leftJoin(resource.roles, role).fetchJoin()
                .orderBy(
                        resource.urlPattern.length().desc(),       // URL 패턴 길이 긴 순서
                        methodPriority(),                          // ALL 메소드 우선순위
                        resource.id.asc()                          // 마지막 ID 순
                )
                .fetch();
    }

    private OrderSpecifier<Integer> methodPriority() {
        return new CaseBuilder()
                .when(resource.method.eq(Method.ALL)).then(1)
                .otherwise(0)
                .asc();
    }

    private BooleanExpression containsUrlPattern(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return resource.urlPattern.contains(keyword.toLowerCase());
    }

    private BooleanExpression eqMethod(Method method) {
        return (method != null && method != Method.ALL)
                ? resource.method.eq(method)
                : null;
    }
}

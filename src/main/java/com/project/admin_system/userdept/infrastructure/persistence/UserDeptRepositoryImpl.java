package com.project.admin_system.userdept.infrastructure.persistence;

import static com.project.admin_system.common.utils.QueryDslUtil.getOrderSpecifier;
import static com.project.admin_system.dept.domain.QDept.dept;
import static com.project.admin_system.user.domain.QUser.user;
import static com.project.admin_system.userdept.domain.QUserDept.userDept;

import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserStatus;
import com.project.admin_system.userdept.application.dto.UserDeptDto;
import com.project.admin_system.userdept.domain.UserDept;

import com.project.admin_system.userdept.domain.UserDeptRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
public class UserDeptRepositoryImpl implements UserDeptRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserDept> findAllByUserIdInWithAll(List<Long> userIds) {
        return queryFactory
                .selectFrom(userDept)
                .join(userDept.user, user).fetchJoin()
                .join(userDept.dept, dept).fetchJoin()
                .where(userDept.user.id.in(userIds))
                .fetch();
    }

    @Override
    public long countActiveUsersInDepts(List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return 0L;
        }

        Long count = queryFactory
                .select(userDept.count())
                .from(userDept)
                .join(userDept.user, user)
                .where(
                        userDept.dept.id.in(deptIds),
                        isManagedUser()
                )
                .fetchOne();

        return count == null ? 0L : count;
    }

    @Override
    public Page<User> findAllByDeptId(Pageable pageable, List<Long> deptIds,
                                      String keyword) {
        List<User> content = queryFactory.selectFrom(user)
                .leftJoin(user.userDept, userDept).fetchJoin()
                .where(
                        containsNameOrUserCode(keyword),
                        isManagedUser(),
                        deptIdsIn(deptIds)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort(), user))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(user.count())
                .from(user)
                .leftJoin(user.userDept, userDept)
                .where(
                        containsNameOrUserCode(keyword),
                        isManagedUser(),
                        deptIdsIn(deptIds)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<UserDeptDto> findUserDeptByIdIn(List<Long> ids) {
        return queryFactory
                .select(Projections.constructor(
                        UserDeptDto.class,
                        user.id,
                        userDept.dept.id))
                .from(user)
                .leftJoin(user.userDept, userDept)
                .where(user.id.in(ids))
                .fetch();
    }

    private BooleanExpression isManagedUser() {
        return user.userStatus.notIn(UserStatus.DELETED, UserStatus.INACTIVE)
                .and(user.deletedAt.isNull());
    }

    private BooleanExpression containsNameOrUserCode(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return user.name.contains(keyword).or(user.userCode.contains(keyword));
    }


    private BooleanExpression deptIdsIn(List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return null;
        }
        return userDept.dept.id.in(deptIds);
    }
}

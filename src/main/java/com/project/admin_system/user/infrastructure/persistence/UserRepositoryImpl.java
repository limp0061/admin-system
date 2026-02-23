package com.project.admin_system.user.infrastructure.persistence;

import static com.project.admin_system.common.utils.QueryDslUtil.getOrderSpecifier;
import static com.project.admin_system.user.domain.QUser.user;
import static com.project.admin_system.userdept.domain.QUserDept.userDept;

import com.project.admin_system.user.application.dto.UserSearchResponse;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserRepositoryCustom;
import com.project.admin_system.user.domain.UserStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> findAllByDeletedAtIsNull(Pageable pageable, UserStatus userStatus, String keyword) {

        List<User> content = queryFactory.selectFrom(user)
                .leftJoin(user.userDept, userDept)
                .fetchJoin()
                .where(
                        containsNameOrEmailId(keyword),
                        eqStatus(userStatus)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort(), user))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(user.count())
                .from(user)
                .where(
                        containsNameOrEmailId(keyword),
                        eqStatus(userStatus)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Long> findAllReadyForDelete(List<Long> ids) {
        return queryFactory.select(user.id)
                .from(user)
                .where(user.id.in(ids),
                        isInActiveUser()
                                .or(isDeletedUser())
                )
                .fetch();
    }

    @Override
    public long countByActiveStatus() {
        Long count = queryFactory.select(user.count())
                .from(user)
                .where(isManagedUser())
                .fetchOne();
        return count == null ? 0L : count;
    }

    @Override
    public Page<User> findAdminsWithIps(Pageable pageable, String keyword) {
        List<User> content = queryFactory.select(user)
                .from(user)
                .leftJoin(user.role).fetchJoin()
                .where(containsNameOrEmailId(keyword),
                        isAdmin())
                .orderBy(getOrderSpecifier(pageable.getSort(), user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(user.count())
                .from(user)
                .where(containsNameOrEmailId(keyword),
                        isAdmin());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<UserSearchResponse> searchAllActiveUsers(String keyword) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        return queryFactory.select(
                        Projections.constructor(UserSearchResponse.class,
                                user.id,
                                user.name,
                                user.emailId,
                                userDept.dept.deptName
                        )
                )
                .from(user)
                .leftJoin(user.userDept, userDept)
                .leftJoin(userDept.dept)
                .where(
                        isActiveUser(),
                        containsNameOrEmailId(keyword))
                .orderBy(getOrderSpecifier(sort, user))
                .limit(10)
                .fetch();
    }

    @Override
    public User findAdminsWithIpsById(Long id) {
        return queryFactory.selectFrom(user)
                .leftJoin(user.userDept, userDept).fetchJoin()
                .leftJoin(userDept.dept).fetchJoin()
                .leftJoin(user.role).fetchJoin()
                .where(user.id.eq(id),
                        isAdmin())
                .fetchOne();

    }

    @Override
    public boolean existsAdminRole(Long id) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(user)
                .where(
                        user.id.eq(id),
                        isAdmin()
                )
                .fetchFirst();

        return fetchOne != null;
    }

    private BooleanExpression isAdmin() {
        return user.role.isAdmin.isTrue();
    }

    private BooleanExpression containsNameOrEmailId(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return user.name.contains(keyword).or(user.emailId.contains(keyword));
    }

    private BooleanExpression eqStatus(UserStatus userStatus) {
        if (userStatus == null) {
            return isManagedUser();
        }
        if (userStatus == UserStatus.DELETED) {
            return user.deletedAt.isNotNull().and(user.userStatus.eq(UserStatus.DELETED));
        }
        return user.deletedAt.isNull().and(user.userStatus.eq(userStatus));
    }

    private BooleanExpression isInActiveUser() {
        return user.userStatus.eq(UserStatus.INACTIVE)
                .and(user.deletedAt.isNull());
    }

    private BooleanExpression isDeletedUser() {
        return user.userStatus.eq(UserStatus.DELETED)
                .and(user.deletedAt.isNotNull());
    }

    private BooleanExpression isManagedUser() {
        return user.deletedAt.isNull()
                .and(user.userStatus.ne(UserStatus.DELETED))
                .and(user.userStatus.ne(UserStatus.INACTIVE));
    }

    private BooleanExpression isActiveUser() {
        return user.deletedAt.isNull()
                .and(user.userStatus.eq(UserStatus.ACTIVE))
                .or(user.userStatus.eq(UserStatus.LOCKED));
    }
}

package com.project.admin_system.resources.infrastructure.persistence;


import static com.project.admin_system.resources.domain.QRole.role;

import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.resources.domain.RoleRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Role> findAllByAdminFilter(boolean onlyAdmin) {

        return queryFactory.selectFrom(role)
                .where(isAdmin(onlyAdmin),
                        role.roleKey.ne("ROLE_ANONYMOUS"))
                .fetch();

    }

    private BooleanExpression isAdmin(boolean onlyAdmin) {
        return onlyAdmin ? role.isAdmin.isTrue() : null;
    }
}

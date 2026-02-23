package com.project.admin_system.dept.infrastructure.persistence;

import static com.project.admin_system.dept.domain.QDept.dept;

import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeptRepositoryImpl implements DeptRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Dept> findAllOrderBySortOrder() {
        return queryFactory
                .selectFrom(dept)
                .orderBy(
                        dept.upperDept.id.asc().nullsFirst(),
                        dept.sortOrder.asc()
                )
                .fetch();
    }
}
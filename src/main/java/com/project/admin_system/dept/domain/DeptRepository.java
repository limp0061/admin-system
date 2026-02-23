package com.project.admin_system.dept.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptRepository extends JpaRepository<Dept, Long>, DeptRepositoryCustom {

    Optional<Dept> findByDeptCode(String deptCode);

    List<Dept> findByUpperDeptId(Long upperDeptId);

    @EntityGraph(attributePaths = {"children"})
    Optional<Dept> findWithChildrenById(Long id);
}

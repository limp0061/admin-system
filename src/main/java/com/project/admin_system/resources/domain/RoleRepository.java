package com.project.admin_system.resources.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {
    Optional<Role> findByRoleKey(String name);

    List<Role> findAllByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"parent", "children"})
    List<Role> findAllByRoleKeyNot(String roleKey);

    @EntityGraph(attributePaths = {"parent", "children"})
    Optional<Role> findById(Long id);

    boolean existsByRoleKey(String roleKey);
}

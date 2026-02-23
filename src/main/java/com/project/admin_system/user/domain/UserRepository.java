package com.project.admin_system.user.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    boolean existsByEmailId(String emailId);

    boolean existsByUserCode(String userCode);

    int countByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"userDept", "userDept.dept"})
    List<User> findAllByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"role", "userDept", "userDept.dept"})
    Optional<User> findWithDeptById(Long id);

    @EntityGraph(attributePaths = {"allowedIps"})
    List<User> findAllWithAllowedIpsByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"role", "userDept", "userDept.dept", "allowedIps"})
    Optional<User> findByEmailId(String username);

    boolean existsByRoleId(Long roleId);
}

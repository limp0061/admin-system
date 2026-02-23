package com.project.admin_system.resources.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcesRepository extends JpaRepository<Resource, Long>, ResourcesRepositoryCustom {
    boolean existsByUrlPatternAndMethod(String urlPattern, Method method);

    int countByIdIn(List<Long> ids);

    boolean existsByUrlPatternAndMethodAndIdNot(String urlPattern, Method method, long id);

    boolean existsByRoles_Id(Long roleId);

    Optional<Resource> findByUrlPatternAndMethod(String urlPattern, Method method);
}

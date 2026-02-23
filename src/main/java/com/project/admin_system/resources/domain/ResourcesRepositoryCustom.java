package com.project.admin_system.resources.domain;


import com.project.admin_system.resources.application.dto.ResourcesSearchRequest;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResourcesRepositoryCustom {
    Page<Resource> findAllOrderBySortOrder(Pageable pageable, ResourcesSearchRequest request);

    List<Resource> findAllWithRoles();
}

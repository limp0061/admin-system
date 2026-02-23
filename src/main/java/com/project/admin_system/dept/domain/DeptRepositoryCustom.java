package com.project.admin_system.dept.domain;

import java.util.List;

public interface DeptRepositoryCustom {

    List<Dept> findAllOrderBySortOrder();
}

package com.project.admin_system.resources.domain;

import java.util.List;

public interface RoleRepositoryCustom {
    List<Role> findAllByAdminFilter(boolean isAdmin);
}

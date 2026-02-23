package com.project.admin_system.userdept.domain;

import com.project.admin_system.user.domain.User;
import com.project.admin_system.userdept.application.dto.UserDeptDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDeptRepositoryCustom {

    Page<User> findAllByDeptId(Pageable pageable, List<Long> ids, String keyword);

    List<UserDeptDto> findUserDeptByIdIn(List<Long> ids);

    List<UserDept> findAllByUserIdInWithAll(List<Long> userIds);

    long countActiveUsersInDepts(List<Long> deptIds);
}

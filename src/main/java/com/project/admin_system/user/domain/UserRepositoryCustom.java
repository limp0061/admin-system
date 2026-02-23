package com.project.admin_system.user.domain;

import com.project.admin_system.user.application.dto.UserSearchResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<User> findAllByDeletedAtIsNull(Pageable pageable,
                                        UserStatus userStatus, String keyword);

    List<Long> findAllReadyForDelete(List<Long> ids);

    long countByActiveStatus();

    Page<User> findAdminsWithIps(Pageable pageable, String keyword);

    List<UserSearchResponse> searchAllActiveUsers(String keyword);

    User findAdminsWithIpsById(Long id);

    boolean existsAdminRole(Long id);
}

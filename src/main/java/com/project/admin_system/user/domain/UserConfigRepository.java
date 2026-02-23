package com.project.admin_system.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long>, UserConfigRepositoryCustom {

    UserConfig findByUserId(long userId);
}

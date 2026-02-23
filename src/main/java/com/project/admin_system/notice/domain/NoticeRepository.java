package com.project.admin_system.notice.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    int countByIdIn(List<Long> ids);

    Notice findByTitle(String title);
}

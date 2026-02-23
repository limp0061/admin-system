package com.project.admin_system.notice.domain;

import com.project.admin_system.notice.application.dto.NoticeListResponse;
import com.project.admin_system.notice.application.dto.NoticeSearchRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    Page<NoticeListResponse> findAllNotice(Pageable pageable, NoticeSearchRequest request);

    List<Notice> findNoticeTop(int limitCount);

    long deleteByIdInBatch(List<Long> ids);
}

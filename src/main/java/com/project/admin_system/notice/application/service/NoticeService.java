package com.project.admin_system.notice.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.ParseService;
import com.project.admin_system.file.application.service.FileService;
import com.project.admin_system.file.domain.DomainType;
import com.project.admin_system.notice.application.dto.NoticeCreatedEvent;
import com.project.admin_system.notice.application.dto.NoticeListResponse;
import com.project.admin_system.notice.application.dto.NoticeSaveRequest;
import com.project.admin_system.notice.application.dto.NoticeSearchRequest;
import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notice.domain.NoticeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ParseService parseService;
    private final FileService fileService;

    public Page<NoticeListResponse> findAllNotice(Pageable pageable, NoticeSearchRequest request) {
        return noticeRepository.findAllNotice(pageable, request);
    }

    public Notice findNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));

    }

    @Transactional
    public void saveNotice(NoticeSaveRequest request) {

        Notice notice = request.toEntity();
        noticeRepository.save(notice);

        List<Long> fileIds = parseService.extractFileIdsFromHtml(notice.getContent());

        fileService.finalizeImages(fileIds, notice.getId(), DomainType.NOTICE);

        if (request.isRealTimeNoticed()) {
            eventPublisher.publishEvent(
                    new NoticeCreatedEvent(notice.getId(), notice.getTitle(),
                            true, request.isForce()));
        }
    }

    public int countByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.NOTICE_NOT_FOUND);
        }

        int count = noticeRepository.countByIdIn(ids);
        if (count == 0) {
            throw new BusinessException(ErrorCode.NOTICE_NOT_FOUND);
        }

        return count;
    }

    public List<NoticeListResponse> findNoticesTop30() {
        List<Notice> list = noticeRepository.findNoticeTop(30);
        return list.stream().map(NoticeListResponse::from)
                .toList();
    }

    @Transactional
    public long deleteNotice(List<Long> ids) {

        fileService.softDeleteFiles(ids, DomainType.NOTICE);

        return noticeRepository.deleteByIdInBatch(ids);
    }
}

package com.project.admin_system.notice.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.ParseService;
import com.project.admin_system.file.application.service.FileService;
import com.project.admin_system.file.domain.DomainType;
import com.project.admin_system.logs.application.dto.AuditLogDetailRequest;
import com.project.admin_system.logs.application.dto.AuditLogUpdateRequest;
import com.project.admin_system.logs.application.service.AuditLogService;
import com.project.admin_system.logs.domain.AuditTarget;
import com.project.admin_system.notice.application.dto.NoticeAuditLog;
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
    private final AuditLogService auditLogService;

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
        NoticeAuditLog before = null;
        if (request.id() != null) {
            Notice existing = noticeRepository.findById(request.id())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOTICE_NOT_FOUND));
            before = NoticeAuditLog.from(existing);
        }

        noticeRepository.save(notice);

        List<Long> fileIds = parseService.extractFileIdsFromHtml(notice.getContent());

        fileService.finalizeImages(fileIds, notice.getId(), DomainType.NOTICE);

        if (request.isRealTimeNoticed()) {
            eventPublisher.publishEvent(
                    new NoticeCreatedEvent(notice.getId(), notice.getTitle(),
                            true, request.isForce()));
        }

        if (request.id() == null) {
            AuditLogDetailRequest detailRequest = new AuditLogDetailRequest(notice.getId(), notice.getTitle(),
                    NoticeAuditLog.from(notice));
            auditLogService.logCreate(AuditTarget.NOTICE, List.of(detailRequest));
        } else {
            AuditLogUpdateRequest updateRequest = new AuditLogUpdateRequest(notice.getId(), notice.getTitle(), before,
                    NoticeAuditLog.from(notice));
            auditLogService.logUpdate(AuditTarget.NOTICE, List.of(updateRequest));
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
        List<AuditLogDetailRequest> detailRequests = noticeRepository.findAllById(ids).stream()
                .map(notice -> new AuditLogDetailRequest(
                        notice.getId(),
                        notice.getTitle(),
                        NoticeAuditLog.from(notice)
                ))
                .toList();

        fileService.softDeleteFiles(ids, DomainType.NOTICE);
        long deleted = noticeRepository.deleteByIdInBatch(ids);

        auditLogService.logDelete(AuditTarget.NOTICE, detailRequests);
        return deleted;
    }
}

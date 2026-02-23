package com.project.admin_system.notice.presentation;

import static com.project.admin_system.common.dto.CustomConstants.HEADER_X_REQUESTED_WITH;
import static com.project.admin_system.common.dto.CustomConstants.XML_HTTP_REQUEST;

import com.project.admin_system.common.dto.PageResponse;
import com.project.admin_system.notice.application.dto.NoticeDetailResponse;
import com.project.admin_system.notice.application.dto.NoticeFilter;
import com.project.admin_system.notice.application.dto.NoticeListResponse;
import com.project.admin_system.notice.application.dto.NoticeSearchRequest;
import com.project.admin_system.notice.application.service.NoticeService;
import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notice.domain.NoticeType;
import com.project.admin_system.notification.application.service.NotificationService;
import com.project.admin_system.notification.domain.NotificationMaster;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final NotificationService notificationService;

    @GetMapping
    public String noticeMain(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Direction.ASC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            NoticeSearchRequest request,
            Model model
    ) {
        Page<NoticeListResponse> list = noticeService.findAllNotice(pageable, request);
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("noticeFilter", NoticeFilter.values());
        model.addAttribute("params", request);
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/notice/notice-main";
        }
        return "page/notice/notice";
    }

    @GetMapping("/modal/add")
    public String modalAdd(
            @RequestParam(name = "mode") String mode,
            Model model
    ) {
        model.addAttribute("notice", NoticeDetailResponse.empty());
        model.addAttribute("templateName", "page/notice/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("types", NoticeType.values());
        model.addAttribute("mode", mode);
        return "components/modal-layout";
    }

    @GetMapping("/modal/edit")
    public String modalEdit(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "mode") String mode,
            Model model
    ) {
        Notice notice = noticeService.findNoticeById(id);

        model.addAttribute("notice", NoticeDetailResponse.from(notice));
        model.addAttribute("templateName", "page/notice/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("types", NoticeType.values());
        model.addAttribute("mode", mode);
        return "components/modal-layout";
    }

    @GetMapping("/modal/delete")
    public String modalDelete(
            @RequestParam(name = "ids") List<Long> ids,
            Model model
    ) {
        model.addAttribute("count", noticeService.countByIds(ids));
        model.addAttribute("ids", ids);
        model.addAttribute("templateName", "page/notice/modal-delete");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }

    @GetMapping("/{noticeId}")
    public String noticeDetail(
            @PathVariable(name = "noticeId") Long noticeId,
            @RequestParam(name = "notificationId", required = false) Long notificationId,
            Model model
    ) {

        Notice notice = noticeService.findNoticeById(noticeId);
        Long targetNotificationId = notificationId;

        if (targetNotificationId == null && notice.isRealtimeNotified()) {
            NotificationMaster master = notificationService.findByNoticeId(noticeId);
            if (master != null) {
                targetNotificationId = master.getId();
            }
        }

        if (targetNotificationId != null) {
            notificationService.markAsRead(targetNotificationId, 1L);
        }

        model.addAttribute("notice", NoticeDetailResponse.from(notice));
        model.addAttribute("templateName", "page/notice/notice-detail");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("types", NoticeType.values());
        return "components/modal-layout";
    }

    @GetMapping("/modal/view")
    public String noticeView(
            Model model
    ) {
        List<NoticeListResponse> list = noticeService.findNoticesTop30();
        model.addAttribute("list", list);
        model.addAttribute("templateName", "page/notice/notice-view-modal");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("types", NoticeType.values());
        return "components/modal-layout";
    }
}

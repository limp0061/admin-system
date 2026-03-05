package com.project.admin_system.logs.presentation;

import static com.project.admin_system.common.dto.CustomConstants.HEADER_X_REQUESTED_WITH;
import static com.project.admin_system.common.dto.CustomConstants.XML_HTTP_REQUEST;

import com.project.admin_system.common.dto.PageResponse;
import com.project.admin_system.logs.application.dto.AuditLogDetailResponse;
import com.project.admin_system.logs.application.dto.AuditLogListResponse;
import com.project.admin_system.logs.application.dto.AuditLogSearchRequest;
import com.project.admin_system.logs.application.service.AuditLogService;
import com.project.admin_system.logs.domain.AuditAction;
import com.project.admin_system.logs.domain.AuditTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogsController {

    private final AuditLogService auditLogService;

    @GetMapping("/audit")
    public String auditLogs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            AuditLogSearchRequest request,
            Model model
    ) {
        AuditLogSearchRequest normalized = request.normalize();

        Page<AuditLogListResponse> list = auditLogService.findAuditLogs(pageable, normalized);
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("params", normalized);
        model.addAttribute("targets", AuditTarget.values());
        model.addAttribute("actions", AuditAction.values());
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/logs/audit/audit-main";
        }
        return "page/logs/audit/audit-list";
    }

    @GetMapping("/audit/modal/detail")
    public String auditModalDetail(
            @RequestParam(name = "id") Long id,
            Model model
    ) {
        AuditLogDetailResponse details = auditLogService.findAuditLogById(id);
        model.addAttribute("auditLog", details);
        model.addAttribute("templateName", "page/logs/audit/modal-detail");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }

    @GetMapping("/activity")
    public String activityLogs() {
        return "page/logs/activity/activity-list";
    }
}

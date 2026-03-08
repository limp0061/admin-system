package com.project.admin_system.logs.presentation;

import static com.project.admin_system.common.dto.CustomConstants.HEADER_X_REQUESTED_WITH;
import static com.project.admin_system.common.dto.CustomConstants.XML_HTTP_REQUEST;

import com.project.admin_system.common.dto.PageResponse;
import com.project.admin_system.history.application.service.LoginHistoryService;
import com.project.admin_system.history.application.service.PasswordHistoryService;
import com.project.admin_system.logs.application.dto.AuditLogDetailResponse;
import com.project.admin_system.logs.application.dto.AuditLogListResponse;
import com.project.admin_system.logs.application.dto.AuditLogSearchRequest;
import com.project.admin_system.logs.application.dto.HistorySearchRequest;
import com.project.admin_system.logs.application.service.AuditLogService;
import com.project.admin_system.logs.domain.AuditAction;
import com.project.admin_system.logs.domain.AuditTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
    private final LoginHistoryService loginHistoryService;
    private final PasswordHistoryService passwordHistoryService;

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

    @GetMapping("/history/login")
    public String loginHistoryLogs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            HistorySearchRequest request,
            Model model
    ) {

        HistorySearchRequest normalized = request.normalize();

        Page<LoginHistoryListResponse> list = loginHistoryService.findLoginHistoryList(pageable, normalized);
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("templateName", "page/logs/history/login-table");
        model.addAttribute("fragmentName", "loginHistoryTable");
        model.addAttribute("params", normalized);
        model.addAttribute("currentMenu", "login");
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/logs/history/history-main";
        }
        return "page/logs/history/history-list";
    }

    @GetMapping("/history/password")
    public String passwordHistoryLogs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            HistorySearchRequest request,
            Model model
    ) {

        HistorySearchRequest normalized = request.normalize();

        Page<PasswordHistoryListResponse> list = passwordHistoryService.findPasswordHistoryList(pageable, normalized);
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("templateName", "page/logs/history/password-table");
        model.addAttribute("fragmentName", "passwordHistoryTable");
        model.addAttribute("params", normalized);
        model.addAttribute("currentMenu", "password");
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/logs/history/history-main";
        }
        return "page/logs/history/history-list";
    }
}

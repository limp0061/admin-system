package com.project.admin_system.scheduler.presentation;

import static com.project.admin_system.common.dto.CustomConstants.HEADER_X_REQUESTED_WITH;
import static com.project.admin_system.common.dto.CustomConstants.XML_HTTP_REQUEST;

import com.project.admin_system.common.dto.ModalViewData;
import com.project.admin_system.common.dto.PageResponse;
import com.project.admin_system.scheduler.application.dto.SchedulerJobDetail;
import com.project.admin_system.scheduler.application.dto.SchedulerJobResponse;
import com.project.admin_system.scheduler.application.service.SchedulerService;
import com.project.admin_system.scheduler.application.service.SchedulerValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/schedulers")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final SchedulerValidator schedulerValidator;

    @GetMapping("/run")
    public String schedulerMain(
            @PageableDefault(size = 10, sort = "lastRunAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            Model model
    ) {
        Page<SchedulerJobResponse> schedulerJobs = schedulerService.findAllSchedulerJobs(pageable);
        model.addAttribute("list", PageResponse.of(schedulerJobs));
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/scheduler/scheduler-main";
        }
        return "page/scheduler/scheduler-list";
    }

    @GetMapping("/modal/confirm")
    public String modalConfirm(
            @RequestParam(name = "ids") List<Long> ids,
            @RequestParam(name = "mode") String mode,
            Model model
    ) {
        String upperMode = mode.toUpperCase();
        ModalViewData data = schedulerValidator.validateForConfirm(ids, upperMode);
        model.addAttribute("message", data.message());
        model.addAttribute("ids", ids);
        model.addAttribute("mode", upperMode);
        model.addAttribute("templateName", "page/scheduler/modal-confirm");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }

    @GetMapping("/modal/detail/{id}")
    public String modalDetail(
            @PathVariable(name = "id") Long id,
            Model model
    ) {
        SchedulerJobDetail detail = schedulerService.getById(id);
        model.addAttribute("detail", detail);
        model.addAttribute("templateName", "page/scheduler/modal-edit-view");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }
}

package com.project.admin_system.dept.presentation;

import static com.project.admin_system.common.dto.CustomConstants.HEADER_X_REQUESTED_WITH;
import static com.project.admin_system.common.dto.CustomConstants.XML_HTTP_REQUEST;

import com.project.admin_system.common.dto.PageResponse;
import com.project.admin_system.dept.application.dto.DeptNode;
import com.project.admin_system.dept.application.dto.DeptResponse;
import com.project.admin_system.userdept.application.dto.DeptSearchRequest;
import com.project.admin_system.dept.application.dto.DeptTreeMode;
import com.project.admin_system.dept.application.dto.DeptValidResponse;
import com.project.admin_system.dept.application.service.DeptService;
import com.project.admin_system.dept.application.validate.DeptValidator;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.user.application.dto.UserListResponse;
import com.project.admin_system.userdept.application.dto.UserDeptResponse;
import com.project.admin_system.userdept.application.service.UserDeptService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("/depts")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;
    private final DeptValidator deptValidator;
    private final UserDeptService userDeptService;

    @GetMapping
    public String deptMain(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            DeptSearchRequest deptSearchRequest,
            Model model
    ) {
        List<Long> ids = new ArrayList<>();
        if (deptSearchRequest.deptId() != null && deptSearchRequest.deptId() != 0) {
            ids = deptService.findAllSubDeptIds(deptSearchRequest.deptId());
        }

        Page<UserDeptResponse> list = userDeptService.findAllByDeptId(pageable, ids, deptSearchRequest.keyword());
        List<DeptNode> deptNodes = deptService.findDeptNodes();
        model.addAttribute("tree", deptNodes);
        model.addAttribute("mode", DeptTreeMode.VIEW);
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("params", deptSearchRequest);
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/dept/dept-main";
        }
        return "page/dept/dept-user";
    }

    @GetMapping("/tree")
    public String deptTree(Model model) {

        List<DeptNode> deptNodes = deptService.findDeptNodes();
        model.addAttribute("mode", DeptTreeMode.EDIT);
        model.addAttribute("tree", deptNodes);
        return "page/dept/dept-tree-view";
    }

    @GetMapping("/modal/add")
    public String modalAdd(
            @RequestParam(name = "upperDeptId", required = false) Long upperDeptId,
            @RequestParam(name = "mode") String mode,
            Model model
    ) {

        List<DeptNode> deptNodes = deptService.selectDeptNodes();
        model.addAttribute("tree", deptNodes);
        model.addAttribute("templateName", "page/dept/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("dept", DeptNode.builder()
                .upperDeptId(upperDeptId)
                .build()
        );
        model.addAttribute("mode", mode);
        return "components/modal-layout";
    }

    @GetMapping("/modal/edit")
    public String editModal(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "mode") String mode,
            Model model
    ) {
        List<DeptNode> deptNodes = deptService.selectDeptNodes();
        model.addAttribute("tree", deptNodes);
        model.addAttribute("templateName", "page/dept/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("dept", DeptResponse.from(deptService.findDeptById(id)));
        model.addAttribute("mode", mode);
        return "components/modal-layout";
    }

    @GetMapping("/modal/delete")
    public String deleteModal(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "mode") String mode,
            Model model
    ) {

        DeptValidResponse deptValidResponse = deptValidator.validateForDelete(id);

        model.addAttribute("result", deptValidResponse);
        model.addAttribute("dept", deptValidResponse.dept());
        model.addAttribute("mode", mode);
        model.addAttribute("templateName", "page/dept/modal-delete");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }
}

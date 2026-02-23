package com.project.admin_system.userdept.presentation;

import com.project.admin_system.common.dto.PageResponse;
import com.project.admin_system.dept.application.dto.DeptNode;
import com.project.admin_system.dept.application.service.DeptService;
import com.project.admin_system.user.application.service.UserService;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.userdept.application.dto.DeptSearchRequest;
import com.project.admin_system.userdept.application.dto.UserDeptDto;
import com.project.admin_system.userdept.application.dto.UserDeptResponse;
import com.project.admin_system.userdept.application.service.UserDeptService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user-depts")
@RequiredArgsConstructor
public class UserDeptController {

    private final DeptService deptService;
    private final UserDeptService userDeptService;
    private final UserService userService;

    @GetMapping("/modal/form")
    public String modalForm(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "ids", required = false) List<Long> userIds,
            Model model) {

        List<User> selectedUsers = (userIds != null && !userIds.isEmpty())
                ? userService.findUsersByIdIn(userIds)
                : new ArrayList<>();

        Page<UserDeptResponse> list = userDeptService.findAllByDeptId(pageable, null, null);
        List<DeptNode> deptNodes = deptService.selectDeptNodes();
        model.addAttribute("tree", deptNodes);
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("selectedUsers", selectedUsers.stream().map(UserDeptResponse::from).toList());
        model.addAttribute("templateName", "page/dept/dept-modal-form");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }

    @GetMapping("/modal/form/table")
    public String modalFormTable(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            DeptSearchRequest deptSearchRequest,
            Model model) {

        List<Long> ids = new ArrayList<>();
        if (deptSearchRequest.deptId() != null && deptSearchRequest.deptId() != 0) {
            ids = deptService.findAllSubDeptIds(deptSearchRequest.deptId());
        }

        Page<UserDeptResponse> list = userDeptService.findAllByDeptId(pageable, ids, deptSearchRequest.keyword());
        List<DeptNode> deptNodes = deptService.selectDeptNodes();
        model.addAttribute("tree", deptNodes);
        model.addAttribute("list", PageResponse.of(list));
        return "page/dept/dept-modal-table";
    }

    @GetMapping("/modal/delete")
    public String deptUserDeleteModal(
            @RequestParam(name = "ids") List<Long> ids,
            @RequestParam(name = "mode") String mode,
            @RequestParam(name = "deptId", required = false) Long deptId,
            Model model
    ) {
        List<UserDeptDto> users = userDeptService.validateUserDepts(ids);
        model.addAttribute("mode", mode);
        model.addAttribute("list", users);
        model.addAttribute("deptId", deptId);
        model.addAttribute("count", users.size());
        model.addAttribute("templateName", "page/dept/dept-modal-delete");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }
}

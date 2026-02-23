package com.project.admin_system.user.presentation;

import static com.project.admin_system.common.dto.CustomConstants.HEADER_X_REQUESTED_WITH;
import static com.project.admin_system.common.dto.CustomConstants.XML_HTTP_REQUEST;

import com.project.admin_system.common.dto.PageResponse;

import com.project.admin_system.resources.application.dto.RoleDto;
import com.project.admin_system.resources.application.service.RoleService;
import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.user.application.dto.AdminRoleResponse;
import com.project.admin_system.user.application.dto.AdminUserListResponse;
import com.project.admin_system.user.application.dto.AdminUserRequest;

import com.project.admin_system.user.application.service.AdminUserService;

import com.project.admin_system.user.application.service.UserService;
import java.util.List;
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
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final UserService userService;
    private final RoleService roleService;
    private final RoleValidator roleValidator;

    @GetMapping
    public String adminMain(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            AdminUserRequest adminUserRequest,
            Model model
    ) {
        Page<AdminUserListResponse> list = adminUserService.findAllByAdminRole(pageable, adminUserRequest.keyword());
        model.addAttribute("list", PageResponse.of(list));
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/admin/admin-main";
        }
        return "page/admin/admin-list";
    }

    @GetMapping("/modal/add")
    public String modalAdd(
            Model model
    ) {
        List<RoleDto> roles = roleService.findAllByAdminFilter(true);
        Role defaultRole = roleValidator.validateRoleKey("ROLE_ADMIN");
        model.addAttribute("templateName", "page/admin/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("roles", roles);
        model.addAttribute("defaultRole", defaultRole);
        return "components/modal-layout";
    }

    @GetMapping("/modal/edit")
    public String editModal(
            @RequestParam(name = "id") Long id,
            Model model
    ) {
        AdminRoleResponse admin = adminUserService.findAdminById(id);
        List<RoleDto> roles = roleService.findAllByAdminFilter(true);

        model.addAttribute("templateName", "page/admin/modal-edit");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("admin", admin);
        model.addAttribute("roles", roles);
        return "components/modal-layout";
    }

    @GetMapping("/modal/delete")
    public String modalDelete(
            @RequestParam(name = "ids") List<Long> ids,
            Model model
    ) {
        model.addAttribute("count", userService.countByIds(ids));
        model.addAttribute("ids", ids);
        model.addAttribute("templateName", "page/admin/modal-delete");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }
}

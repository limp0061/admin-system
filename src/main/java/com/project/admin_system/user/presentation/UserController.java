package com.project.admin_system.user.presentation;

import static com.project.admin_system.common.dto.CustomConstants.HEADER_X_REQUESTED_WITH;
import static com.project.admin_system.common.dto.CustomConstants.XML_HTTP_REQUEST;

import com.project.admin_system.common.dto.PageResponse;
import com.project.admin_system.common.dto.PageType;
import com.project.admin_system.dept.application.service.DeptService;
import com.project.admin_system.file.application.service.FileService;
import com.project.admin_system.resources.application.dto.RoleDto;
import com.project.admin_system.resources.application.service.RoleService;
import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.user.application.dto.UserListResponse;
import com.project.admin_system.user.application.dto.UserSearchRequest;
import com.project.admin_system.user.application.service.UserService;
import com.project.admin_system.user.domain.Gender;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserStatus;
import com.project.admin_system.user.domain.UserStatusMode;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final DeptService deptService;
    private final UserService userService;

    private final FileService fileService;
    private final RoleService roleService;
    private final RoleValidator roleValidator;

    @GetMapping
    public String userMain(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            UserSearchRequest userSearchRequest,
            Model model
    ) {
        Page<UserListResponse> list = userService.findAllByDeletedAtIsNull(pageable, userSearchRequest.status(),
                userSearchRequest.keyword());
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("params", userSearchRequest);
        model.addAttribute("pageType", PageType.LIST);
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/user/user-main";
        }

        return "page/user/user-list";
    }

    @GetMapping("/approvals")
    public String userApprovalsMain(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            UserSearchRequest userSearchRequest,
            Model model
    ) {
        Page<UserListResponse> list = userService.findAllByDeletedAtIsNull(pageable, UserStatus.INACTIVE,
                userSearchRequest.keyword());
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("params", userSearchRequest);
        model.addAttribute("pageType", PageType.APPROVAL);
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/user/user-main";
        }
        return "page/user/user-approvals";
    }

    @GetMapping("/deleted")
    public String userDeletedMain(
            @PageableDefault(size = 10, sort = "deletedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            UserSearchRequest userSearchRequest,
            Model model
    ) {
        Page<UserListResponse> list = userService.findAllByDeletedAtIsNull(pageable, UserStatus.DELETED,
                userSearchRequest.keyword());
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("params", userSearchRequest);
        model.addAttribute("pageType", PageType.DELETED);
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/user/user-main";
        }
        return "page/user/user-deleted";
    }

    @GetMapping("/modal/add")
    public String modalAdd(
            Model model
    ) {
        List<RoleDto> roles = roleService.findAllByAdminFilter(false);
        Role defaultRole = roleValidator.validateRoleKey("ROLE_USER");

        model.addAttribute("templateName", "page/user/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("tree", deptService.selectDeptNodes());
        model.addAttribute("userStatuses", UserStatus.values());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("roles", roles);
        model.addAttribute("defaultRole", defaultRole);
        return "components/modal-layout";
    }

    @GetMapping("/modal/edit")
    public String editModal(
            @RequestParam(name = "id") Long id,
            Model model
    ) {
        User user = userService.findUserById(id);
        String profilePath = fileService.getFilePath(user.getProfilePath());
        List<RoleDto> roles = roleService.findAllByAdminFilter(false);

        model.addAttribute("templateName", "page/user/modal-edit");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("user", UserListResponse.from(user, profilePath));
        model.addAttribute("tree", deptService.selectDeptNodes());
        model.addAttribute("userStatuses", UserStatus.values());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("roles", roles);
        return "components/modal-layout";
    }

    @GetMapping("/modal/changeStatus")
    public String deleteModal(
            @RequestParam(name = "ids") List<Long> ids,
            @RequestParam(name = "mode") String mode,
            Model model
    ) {
        model.addAttribute("templateName", "page/user/modal-change");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("count", userService.countByIds(ids));
        model.addAttribute("ids", ids);

        model.addAttribute("mode", UserStatusMode.valueOf(mode));
        return "components/modal-layout";
    }
}

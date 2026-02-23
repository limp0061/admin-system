package com.project.admin_system.resources.presentation;

import static com.project.admin_system.common.dto.CustomConstants.HEADER_X_REQUESTED_WITH;
import static com.project.admin_system.common.dto.CustomConstants.XML_HTTP_REQUEST;

import com.project.admin_system.common.dto.PageResponse;
import com.project.admin_system.resources.application.dto.ResourcesListResponse;
import com.project.admin_system.resources.application.dto.ResourcesSearchRequest;
import com.project.admin_system.resources.application.dto.RoleTreeDto;
import com.project.admin_system.resources.application.dto.RoleValidResponse;
import com.project.admin_system.resources.application.service.ResourcesService;
import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Method;
import com.project.admin_system.resources.application.dto.RoleDto;
import com.project.admin_system.resources.application.service.RoleService;
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
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourcesController {

    private final ResourcesService resourcesService;
    private final RoleService roleService;
    private final RoleValidator roleValidator;


    @GetMapping("/access")
    public String accessMain(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = HEADER_X_REQUESTED_WITH, required = false) String requestedWith,
            ResourcesSearchRequest request,
            Model model
    ) {
        Page<ResourcesListResponse> list = resourcesService.getAllResources(pageable, request);
        model.addAttribute("list", PageResponse.of(list));
        model.addAttribute("methods", Method.values());
        model.addAttribute("params", request);
        if (XML_HTTP_REQUEST.equals(requestedWith)) {
            return "page/resource/access/access-main";
        }
        return "page/resource/access/access-list";
    }

    @GetMapping("/access/modal/add")
    public String modalAdd(
            Model model
    ) {
        List<RoleDto> roles = roleService.findAllByAdminFilter(true);
        model.addAttribute("templateName", "page/resource/access/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("methods", Method.values());
        model.addAttribute("roles", roles);
        return "components/modal-layout";
    }

    @GetMapping("/access/modal/edit")
    public String modalEdit(
            @RequestParam(name = "id") Long id,
            Model model
    ) {
        List<RoleDto> roles = roleService.findAllByAdminFilter(true);
        ResourcesListResponse resourceDetail = resourcesService.getResourceDetail(id);
        model.addAttribute("templateName", "page/resource/access/modal-edit");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("resource", resourceDetail);
        model.addAttribute("methods", Method.values());
        model.addAttribute("roles", roles);
        return "components/modal-layout";
    }

    @GetMapping("/access/modal/delete")
    public String modalDelete(
            @RequestParam(name = "ids") List<Long> ids,
            Model model
    ) {
        model.addAttribute("count", resourcesService.countByIds(ids));
        model.addAttribute("ids", ids);
        model.addAttribute("templateName", "page/resource/access/modal-delete");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }

    @GetMapping("/role")
    public String roleMain(
            Model model
    ) {
        List<RoleTreeDto> roleTree = roleService.getRoleTree();
        model.addAttribute("tree", roleTree);
        return "page/resource/role/role-list";
    }

    @GetMapping("/role/modal/add")
    public String modalAdd(
            @RequestParam(name = "parentId", required = false) Long parentId,
            @RequestParam(name = "mode") String mode,
            Model model
    ) {

        List<RoleTreeDto> roles = roleService.selectRoleTree();
        model.addAttribute("tree", roles);
        model.addAttribute("templateName", "page/resource/role/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("role", RoleTreeDto.setParentId(parentId));
        model.addAttribute("mode", mode);
        return "components/modal-layout";
    }

    @GetMapping("/role/modal/edit")
    public String editModal(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "mode") String mode,
            Model model
    ) {
        List<RoleTreeDto> roles = roleService.selectRoleTree();
        model.addAttribute("tree", roles);
        model.addAttribute("templateName", "page/resource/role/modal-add");
        model.addAttribute("fragmentName", "content");
        model.addAttribute("role", RoleTreeDto.from(roleService.findRoleById(id)));
        model.addAttribute("mode", mode);
        return "components/modal-layout";
    }

    @GetMapping("/role/modal/delete")
    public String deleteModal(
            @RequestParam(name = "id") Long id,
            Model model
    ) {

        RoleValidResponse roleDto = roleValidator.validateForDelete(id);

        model.addAttribute("result", roleDto);
        model.addAttribute("templateName", "page/resource/role/modal-delete");
        model.addAttribute("fragmentName", "content");
        return "components/modal-layout";
    }
}

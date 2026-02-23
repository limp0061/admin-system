package com.project.admin_system.resources.presentation;

import com.project.admin_system.common.dto.ApiResponse;
import com.project.admin_system.resources.application.dto.ResourcesSaveRequest;
import com.project.admin_system.resources.application.dto.RoleSaveRequest;
import com.project.admin_system.resources.application.service.ResourcesService;
import com.project.admin_system.resources.application.service.RoleService;
import com.project.admin_system.resources.application.validate.RoleValidator;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourcesApiController {

    private final ResourcesService resourcesService;
    private final RoleService roleService;
    private final RoleValidator roleValidator;

    @PostMapping("/access")
    public ResponseEntity<ApiResponse<Void>> saveResources(
            @RequestBody @Valid ResourcesSaveRequest request
    ) {
        resourcesService.saveResource(request);
        String message = "리소스를 추가했습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping("/access/{resourceId}/update")
    public ResponseEntity<ApiResponse<Void>> updateResources(
            @PathVariable(value = "resourceId") Long resourceId,
            @RequestBody @Valid ResourcesSaveRequest request
    ) {
        resourcesService.updateResource(request, resourceId);
        String message = "리소스를 수정했습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @DeleteMapping("/access")
    public ResponseEntity<ApiResponse<Void>> deleteResources(
            @RequestParam(name = "ids") List<Long> ids
    ) {
        long count = resourcesService.deleteResource(ids);
        return ResponseEntity.ok(new ApiResponse<>(count + "개의 리소스가 삭제되었습니다."));
    }

    @PostMapping("/role")
    public ResponseEntity<ApiResponse<Void>> saveRole(
            @RequestBody @Valid RoleSaveRequest request
    ) {
        roleValidator.duplicateRoleCheck(request.roleKey());
        roleService.saveRole(request);
        String message = "권한을 추가했습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping("/role/{roleId}/update")
    public ResponseEntity<ApiResponse<Void>> updateRole(
            @PathVariable(value = "roleId") Long roleId,
            @RequestBody @Valid RoleSaveRequest request
    ) {
        roleService.updateRole(request, roleId);
        String message = "리소스를 수정했습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @DeleteMapping("/role")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @RequestParam(name = "id") Long id
    ) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(new ApiResponse<>("권한이 삭제되었습니다."));
    }
}

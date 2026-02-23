package com.project.admin_system.user.presentation;

import com.project.admin_system.common.dto.ApiResponse;
import com.project.admin_system.user.application.dto.AdminRoleRequest;
import com.project.admin_system.user.application.service.AdminUserService;
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
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminUserApiController {

    private final AdminUserService adminUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveAdmin(
            @RequestBody @Valid AdminRoleRequest request
    ) {

        adminUserService.saveAdmin(request);

        String message = "관리자 설정이 저장되었습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping("/{userId}/update")
    public ResponseEntity<ApiResponse<Void>> updateAdmin(
            @PathVariable(value = "userId") Long userId,
            @RequestBody @Valid AdminRoleRequest request
    ) {

        adminUserService.updateAdmin(userId, request);

        String message = "관리자 설정이 변경되었습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(
            @RequestParam(name = "ids") List<Long> ids
    ) {
        long count = adminUserService.deleteAdmin(ids);
        return ResponseEntity.ok(new ApiResponse<>(count + "명의 관리자 권한이 삭제되었습니다."));
    }
}

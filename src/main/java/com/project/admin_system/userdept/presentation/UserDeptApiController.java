package com.project.admin_system.userdept.presentation;

import com.project.admin_system.common.dto.ApiResponse;
import com.project.admin_system.userdept.application.dto.UserDeptRequest;
import com.project.admin_system.userdept.application.service.UserDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-depts")
@RequiredArgsConstructor
public class UserDeptApiController {

    private final UserDeptService userDeptService;

    @PostMapping("/change")
    public ResponseEntity<ApiResponse<Void>> changeDeptUser(
            @RequestBody UserDeptRequest request
    ) {
        userDeptService.changeUserDept(request);
        String message = "EDIT".equals(request.mode()) ? "구성원을 변경했습니다" : "구성원을 제거하였습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }
}

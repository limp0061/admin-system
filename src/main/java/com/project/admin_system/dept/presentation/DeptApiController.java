package com.project.admin_system.dept.presentation;

import com.project.admin_system.common.dto.ApiResponse;
import com.project.admin_system.dept.application.dto.DeptSaveRequest;
import com.project.admin_system.dept.application.service.DeptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/depts")
@RequiredArgsConstructor
public class DeptApiController {

    private final DeptService deptService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveDept(
            @RequestBody @Valid DeptSaveRequest deptSaveRequest
    ) {

        String message;
        if (deptSaveRequest.id() == null) {
            deptService.createDept(deptSaveRequest);
            message = "부서가 수정되었습니다";
        } else {
            deptService.updateDept(deptSaveRequest);
            message = "부서가 추가되었습니다";
        }

        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @DeleteMapping("/{deptId}")
    public ResponseEntity<ApiResponse<Void>> deleteDept(
            @PathVariable(name = "deptId") Long deptId
    ) {
        deptService.deleteById(deptId);
        return ResponseEntity.ok(new ApiResponse<>("부서가 삭제되었습니다."));
    }
}

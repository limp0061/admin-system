package com.project.admin_system.scheduler.presentation;

import com.project.admin_system.common.dto.ApiResponse;
import com.project.admin_system.scheduler.application.dto.SchedulerJobUpdateRequest;
import com.project.admin_system.scheduler.application.dto.SchedulerResumeRequest;
import com.project.admin_system.scheduler.application.dto.SchedulerRunRequest;
import com.project.admin_system.scheduler.application.dto.SchedulerSuspendRequest;
import com.project.admin_system.scheduler.application.service.SchedulerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/schedulers")
@RequiredArgsConstructor
public class SchedulerApiController {

    private final SchedulerService schedulerService;

    @PostMapping("/run")
    public ResponseEntity<ApiResponse<Void>> execute(
            @Valid @RequestBody SchedulerRunRequest request
    ) {
        schedulerService.execute(request);

        String message = "스케줄러가 실행되었습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping("/suspend")
    public ResponseEntity<ApiResponse<Void>> suspend(
            @Valid @RequestBody SchedulerSuspendRequest request
    ) {
        schedulerService.suspend(request);

        String message = "스케줄러가 비활성화 되었습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping("/resume")
    public ResponseEntity<ApiResponse<Void>> resume(
            @Valid @RequestBody SchedulerResumeRequest request
    ) {
        schedulerService.resume(request);

        String message = "스케줄러가 활성화 되었습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateScheduler(
            @PathVariable Long id,
            @Valid @RequestBody SchedulerJobUpdateRequest request
    ) {
        schedulerService.updateScheduler(id, request);
        String message = "스케줄러 정보가 수정되었습니다.";
        return ResponseEntity.ok().body(new ApiResponse<>(message));
    }
}

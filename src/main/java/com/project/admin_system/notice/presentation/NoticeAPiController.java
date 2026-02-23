package com.project.admin_system.notice.presentation;

import com.project.admin_system.common.dto.ApiResponse;
import com.project.admin_system.notice.application.dto.NoticeSaveRequest;
import com.project.admin_system.notice.application.service.NoticeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeAPiController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveNotice(
            @RequestBody @Valid NoticeSaveRequest noticeSaveRequest
    ) {

        noticeService.saveNotice(noticeSaveRequest);
        String message = noticeSaveRequest.id() != null ? "공지가 수정되었습니다" : "공지가 추가되었습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @RequestParam(name = "ids") List<Long> ids
    ) {
        long count = noticeService.deleteNotice(ids);
        return ResponseEntity.ok(new ApiResponse<>(count + "건의 공지가 삭제되었습니다."));
    }
}

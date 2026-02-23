package com.project.admin_system.user.presentation;

import com.project.admin_system.common.dto.ApiResponse;
import com.project.admin_system.user.application.dto.UserCreateRequest;
import com.project.admin_system.user.application.dto.UserSearchResponse;
import com.project.admin_system.user.application.dto.UserStatusChangeRequest;
import com.project.admin_system.user.application.dto.UserUpdateRequest;
import com.project.admin_system.user.application.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Void>> saveUser(
            @RequestPart(name = "userData") @Valid UserCreateRequest userSaveRequest,
            @RequestPart(name = "profileImage", required = false) MultipartFile profileImage
    ) {
        userService.createUser(userSaveRequest, profileImage);

        String message = "사용자가 추가되었습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping(value = "/{id}/update", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestPart(name = "userData") @Valid UserUpdateRequest userUpdateRequest,
            @RequestPart(name = "profileImage", required = false) MultipartFile profileImage
    ) {
        userService.updateUser(id, userUpdateRequest, profileImage);

        String message = "사용자 정보가 수정되었습니다";
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @PostMapping("/changeStatus")
    public ResponseEntity<ApiResponse<Void>> changeUserStatus(
            @RequestBody UserStatusChangeRequest request
    ) {
        userService.updateUserStatus(request);
        String message = switch (request.mode()) {
            case "DELETED", "REMOVE", "REJECT" -> "사용자 정보가 삭제되었습니다.";
            case "APPROVE" -> "사용자가 승인되었습니다.";
            case "UNLOCKED" -> "사용자의 잠금이 해제되었습니다.";
            default -> "사용자의 상태를 변경하였습니다.";
        };
        return ResponseEntity.ok(new ApiResponse<>(message));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(
            @RequestParam(name = "keyword") String keyword
    ) {
        List<UserSearchResponse> result = userService.searchAllActiveUsers(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContextHolderStrategy().getContext()
                .getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login";
    }
}

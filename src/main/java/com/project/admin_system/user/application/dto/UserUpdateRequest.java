package com.project.admin_system.user.application.dto;

import com.project.admin_system.user.domain.Gender;

import com.project.admin_system.user.domain.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "이름: 필수 정보입니다.")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
        String name,

        String password,

        @Email
        @NotBlank(message = "이메일: 필수 정보입니다.")
        @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.")
        String emailId,

        Long deptId,

        String position,

        String userCode,

        @NotNull(message = "성별: 선택해주세요")
        Gender gender,

        @NotNull(message = "사용자 상태: 선택해주세요")
        UserStatus userStatus,

        Long roleId
) {
}

package com.project.admin_system.user.application.dto;

import com.project.admin_system.user.domain.Gender;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

        @NotBlank(message = "이름: 필수 정보입니다.")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "비밀번호: 필수 정보입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$",
                message = "비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다."
        )
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

        String profilePath,

        Long roleId
) {
    public User toEntity() {
        return User.builder()
                .name(name)
                .password(password)
                .emailId(emailId)
                .userCode(userCode)
                .position(position)
                .gender(gender)
                .userStatus(userStatus)
                .profilePath(profilePath)
                .build();
    }
}

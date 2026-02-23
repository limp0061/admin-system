package com.project.admin_system.user.application.service;

import static com.project.admin_system.common.dto.RedisConstants.USER_CONFIG_PREFIX;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.RedisManager;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.file.application.service.FileService;
import com.project.admin_system.file.domain.DomainType;
import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.user.application.dto.UserConfigDto;
import com.project.admin_system.user.application.dto.UserCreateRequest;
import com.project.admin_system.user.application.dto.UserListResponse;
import com.project.admin_system.user.application.dto.UserSearchResponse;
import com.project.admin_system.user.application.dto.UserStatusChangeRequest;
import com.project.admin_system.user.application.dto.UserUpdateRequest;
import com.project.admin_system.user.application.validate.UserValidator;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserConfig;
import com.project.admin_system.user.domain.UserConfigRepository;
import com.project.admin_system.user.domain.UserRepository;
import com.project.admin_system.user.domain.UserStatus;
import com.project.admin_system.user.domain.UserStatusMode;
import com.project.admin_system.userdept.domain.UserDept;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final DeptRepository deptRepository;
    private final RoleValidator roleValidator;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final RedisManager redisManager;
    private final UserConfigRepository userConfigRepository;

    @Transactional
    public void createUser(UserCreateRequest dto, MultipartFile profileImage) {

        // 이메일 중복 체크
        userValidator.validateDuplicateEmailId(dto.emailId());

        // 사번 중복 체크
        userValidator.validateDuplicateUserCode(dto.userCode());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.password());

        // 부서 저장
        User user = dto.toEntity();
        if (dto.deptId() != null) {
            Dept dept = deptRepository.findById(dto.deptId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DEPT_CODE_NOT_FOUND));
            UserDept userDept = UserDept.builder()
                    .dept(dept)
                    .build();
            user.assignDepartment(userDept);
        }

        Role role = roleValidator.validateRole(dto.roleId());
        user.assignRole(role);

        user.initDefaultConfig();
        user.encPassword(encodedPassword);
        userRepository.save(user);

        if (profileImage != null && !profileImage.isEmpty()) {
            String profilePath = fileService.fileUpload(profileImage, DomainType.PROFILE, user.getId());
            user.updateProfilePath(profilePath);
        }
    }

    @Transactional
    public void updateUser(Long id, UserUpdateRequest dto, MultipartFile profileImage) {

        // 값 비교를 위한 기존 데이터
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getEmailId().equals(dto.emailId())) {
            userValidator.validateDuplicateEmailId(dto.emailId());
        }

        if (!user.getUserCode().equals(dto.userCode())) {
            userValidator.validateDuplicateUserCode(dto.userCode());
        }

        Dept dept = (dto.deptId() != null) ?
                deptRepository.findById(dto.deptId()).orElse(null) : null;

        Role role = roleValidator.validateRole(dto.roleId());
        user.assignRole(role);

        user.update(dto, dept, role);

        if (dto.password() != null && !dto.password().isBlank()) {
            String encodedPassword = passwordEncoder.encode(dto.password());
            user.encPassword(encodedPassword);
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            if (user.getProfilePath() != null) {
                fileService.deleteFile(user.getProfilePath());
            }

            String profilePath = fileService.fileUpload(profileImage, DomainType.PROFILE, user.getId());
            user.updateProfilePath(profilePath);
        }
    }

    public Page<UserListResponse> findAllByDeletedAtIsNull(Pageable pageable, UserStatus userStatus, String keyword) {
        Page<User> users = userRepository.findAllByDeletedAtIsNull(pageable, userStatus, keyword);

        return users.map(UserListResponse::from);
    }

    public User findUserById(Long id) {
        return userRepository.findWithDeptById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public int countByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        int count = userRepository.countByIdIn(ids);
        if (count == 0) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return count;
    }

    public List<User> findUsersByIdIn(List<Long> ids) {
        List<User> users = userRepository.findAllByIdIn(ids);
        if (users == null || users.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return users;
    }

    @Transactional
    public void updateUserStatus(UserStatusChangeRequest request) {
        UserStatusMode userStatus = UserStatusMode.valueOf(request.mode());
        List<Long> ids = request.ids();

        if (userStatus == UserStatusMode.REMOVE || userStatus == UserStatusMode.REJECT) {
            List<Long> validIds = userValidator.validateForDelete(ids);
            userRepository.deleteAllById(validIds);
        } else {
            List<User> users = findUsersByIdIn(ids);
            UserStatus target = switch (userStatus) {
                case APPROVE, RECOVER, UNLOCKED -> UserStatus.ACTIVE;
                case DELETED -> UserStatus.DELETED;
                default -> UserStatus.valueOf(userStatus.name());
            };
            users.forEach(user -> user.updateUserStatus(target));
        }
    }

    public List<UserSearchResponse> searchAllActiveUsers(String keyword) {
        return userRepository.searchAllActiveUsers(keyword);
    }

    @Async
    @Transactional
    public void successLoginHandle(String emailId) {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.loginSuccess();

        UserConfig userConfig = userConfigRepository.findByUserId(user.getId());
        UserConfigDto config = UserConfigDto.from(userConfig);
        redisManager.setData(USER_CONFIG_PREFIX + user.getId(), config);
    }

    @Transactional
    public void handleLoginFailure(String emailId) {
        userRepository.findByEmailId(emailId).ifPresent(user -> {
            user.loginFailure();
            log.warn("Login failed for user: {}. fail count: {}", emailId, user.getPasswordFailCount());
        });
    }
}

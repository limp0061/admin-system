package com.project.admin_system.user.application.service;

import static com.project.admin_system.security.utils.NetworkUtils.getClientIp;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.file.application.service.FileService;
import com.project.admin_system.file.domain.DomainType;
import com.project.admin_system.history.application.service.PasswordHistoryService;
import com.project.admin_system.history.domain.PasswordChangeType;
import com.project.admin_system.history.domain.PasswordHistory;
import com.project.admin_system.logs.application.dto.AuditLogDetailRequest;
import com.project.admin_system.logs.application.dto.AuditLogUpdateRequest;
import com.project.admin_system.logs.application.service.AuditLogService;
import com.project.admin_system.logs.domain.AuditTarget;
import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.user.application.dto.UserAuditLog;
import com.project.admin_system.user.application.dto.UserCreateRequest;
import com.project.admin_system.user.application.dto.UserListResponse;
import com.project.admin_system.user.application.dto.UserSearchResponse;
import com.project.admin_system.user.application.dto.UserStatusChangeRequest;
import com.project.admin_system.user.application.dto.UserUpdateRequest;
import com.project.admin_system.user.application.validate.UserValidator;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserRepository;
import com.project.admin_system.user.domain.UserStatus;
import com.project.admin_system.user.domain.UserStatusMode;
import com.project.admin_system.userdept.domain.UserDept;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final AuditLogService auditLogService;
    private final PasswordHistoryService passwordHistoryService;

    @Transactional
    public void createUser(HttpServletRequest request, UserCreateRequest dto, MultipartFile profileImage) {

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

        PasswordHistory passwordHistory = PasswordHistory.builder()
                .userId(user.getId())
                .emailId(dto.emailId())
                .clientIp(getClientIp(request))
                .password(encodedPassword)
                .changeType(PasswordChangeType.INITIAL)
                .build();

        passwordHistoryService.savePasswordHistory(passwordHistory);

        if (profileImage != null && !profileImage.isEmpty()) {
            log.info("프로필 이미지 업로드 | userId: {}", user.getId());
            String profilePath = fileService.fileUpload(profileImage, DomainType.PROFILE, user.getId());
            user.updateProfilePath(profilePath);
        }

        AuditLogDetailRequest detailRequest = new AuditLogDetailRequest(user.getId(), user.getEmailId(),
                UserAuditLog.from(user));
        auditLogService.logCreate(AuditTarget.USER, List.of(detailRequest));
    }

    @Transactional
    public void updateUser(HttpServletRequest request, Long id, UserUpdateRequest dto, MultipartFile profileImage) {

        // 값 비교를 위한 기존 데이터
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getEmailId().equals(dto.emailId())) {
            userValidator.validateDuplicateEmailId(dto.emailId());
        }

        if (!user.getUserCode().equals(dto.userCode())) {
            userValidator.validateDuplicateUserCode(dto.userCode());
        }

        UserAuditLog before = UserAuditLog.from(user);

        Dept dept = (dto.deptId() != null) ?
                deptRepository.findById(dto.deptId()).orElse(null) : null;

        Role role = roleValidator.validateRole(dto.roleId());
        user.assignRole(role);

        user.update(dto, dept, role);

        if (dto.password() != null && !dto.password().isBlank()) {
            String encodedPassword = passwordEncoder.encode(dto.password());
            user.encPassword(encodedPassword);

            PasswordHistory passwordHistory = PasswordHistory.builder()
                    .userId(user.getId())
                    .emailId(dto.emailId())
                    .clientIp(getClientIp(request))
                    .password(encodedPassword)
                    .changeType(PasswordChangeType.ADMIN)
                    .build();

            passwordHistoryService.savePasswordHistory(passwordHistory);
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            if (user.getProfilePath() != null) {
                log.info("프로필 이미지 삭제 | userId: {}", user.getId());
                fileService.deleteFile(user.getProfilePath());
            }

            log.info("프로필 이미지 업로드 | userId: {}", user.getId());
            String profilePath = fileService.fileUpload(profileImage, DomainType.PROFILE, user.getId());
            user.updateProfilePath(profilePath);
        }

        UserAuditLog after = UserAuditLog.from(user);
        auditLogService.logUpdate(AuditTarget.USER,
                List.of(new AuditLogUpdateRequest(user.getId(), user.getEmailId(), before, after)));
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
            List<User> users = userRepository.findAllById(validIds);

            List<AuditLogDetailRequest> detailRequests = users.stream()
                    .map(user -> new AuditLogDetailRequest(user.getId(), user.getEmailId(), UserAuditLog.from(user)))
                    .toList();

            userRepository.deleteAllById(validIds);
            auditLogService.logDelete(AuditTarget.USER, detailRequests);
        } else {
            List<User> users = findUsersByIdIn(ids);
            UserStatus target = switch (userStatus) {
                case APPROVE, RECOVER, UNLOCKED -> UserStatus.ACTIVE;
                case DELETED -> UserStatus.DELETED;
                default -> UserStatus.valueOf(userStatus.name());
            };
            List<AuditLogUpdateRequest> updateRequests = new ArrayList<>();
            for (User user : users) {
                UserAuditLog before = UserAuditLog.from(user);
                user.updateUserStatus(target);
                UserAuditLog after = UserAuditLog.from(user);
                updateRequests.add(new AuditLogUpdateRequest(user.getId(), user.getEmailId(),
                        before, after));
            }

            auditLogService.logUpdate(AuditTarget.USER, updateRequests);
        }
    }

    public List<UserSearchResponse> searchAllActiveUsers(String keyword) {
        return userRepository.searchAllActiveUsers(keyword);
    }
}

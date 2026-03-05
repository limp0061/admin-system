package com.project.admin_system.user.application.service;

import static com.project.admin_system.user.application.validate.UserValidator.validateIps;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.logs.application.dto.AuditLogDetailRequest;
import com.project.admin_system.logs.application.dto.AuditLogUpdateRequest;
import com.project.admin_system.logs.application.service.AuditLogService;
import com.project.admin_system.logs.domain.AuditTarget;
import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.user.application.dto.AdminAuditLog;
import com.project.admin_system.user.application.dto.AdminRoleRequest;
import com.project.admin_system.user.application.dto.AdminRoleResponse;
import com.project.admin_system.user.application.dto.AdminUserListResponse;
import com.project.admin_system.user.application.validate.UserValidator;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleValidator roleValidator;
    private final UserValidator userValidator;
    private final AuditLogService auditLogService;

    public Page<AdminUserListResponse> findAllByAdminRole(Pageable pageable, String keyword) {
        Page<User> adminsWithIps = userRepository.findAdminsWithIps(pageable, keyword);
        return adminsWithIps.map(AdminUserListResponse::from);
    }

    @Transactional
    public void saveAdmin(AdminRoleRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        userValidator.validateAdminRole(user.getId());

        validateIps(request.ips());

        Role role = roleValidator.validateRole(request.roleId());

        user.addRole(role);
        user.addIps(request.ips());

        AuditLogDetailRequest detailRequest = new AuditLogDetailRequest(user.getId(), user.getEmailId(),
                AdminAuditLog.from(user));
        auditLogService.logCreate(AuditTarget.ADMIN, List.of(detailRequest));
    }

    @Transactional
    public void updateAdmin(Long userId, AdminRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        validateIps(request.ips());

        Role role = roleValidator.validateRole(request.roleId());

        AdminAuditLog before = AdminAuditLog.from(user);

        user.addRole(role);
        user.addIps(request.ips());

        AdminAuditLog after = AdminAuditLog.from(user);
        AuditLogUpdateRequest updateRequest = new AuditLogUpdateRequest(user.getId(), user.getEmailId(), before, after);
        auditLogService.logUpdate(AuditTarget.ADMIN, List.of(updateRequest));
    }


    public AdminRoleResponse findAdminById(Long id) {
        User user = userRepository.findAdminsWithIpsById(id);
        return AdminRoleResponse.from(user);
    }

    @Transactional
    public long deleteAdmin(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        List<AuditLogDetailRequest> detailRequests = new ArrayList<>();
        for (User user : userRepository.findAllWithAllowedIpsByIdIn(ids)) {

            AdminAuditLog detailRequest = AdminAuditLog.from(user);
            Role defaultRole = roleValidator.validateRoleKey("ROLE_USER");
            user.resetToDefaultRole(defaultRole);

            detailRequests.add(new AuditLogDetailRequest(user.getId(), user.getEmailId(), detailRequest));
        }

        auditLogService.logDelete(AuditTarget.ADMIN, detailRequests);

        return ids.size();
    }
}

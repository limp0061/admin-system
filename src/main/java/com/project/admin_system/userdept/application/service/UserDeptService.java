package com.project.admin_system.userdept.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.logs.audit.application.dto.AuditLogUpdateRequest;
import com.project.admin_system.logs.audit.application.service.AuditLogService;
import com.project.admin_system.logs.audit.domain.AuditTarget;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserRepository;
import com.project.admin_system.userdept.application.dto.UserDeptAuditLog;
import com.project.admin_system.userdept.application.dto.UserDeptDto;
import com.project.admin_system.userdept.application.dto.UserDeptRequest;
import com.project.admin_system.userdept.application.dto.UserDeptResponse;
import com.project.admin_system.userdept.domain.UserDept;
import com.project.admin_system.userdept.domain.UserDeptRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDeptService {

    private final UserDeptRepository userDeptRepository;
    private final DeptRepository deptRepository;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public List<UserDeptDto> validateUserDepts(List<Long> ids) {
        List<UserDeptDto> users = userDeptRepository.findUserDeptByIdIn(ids);
        if (users.size() != ids.size()) {
            throw new BusinessException(ErrorCode.INVALID_IDS_CONTAIN);
        }
        return users;
    }

    public Page<UserDeptResponse> findAllByDeptId(Pageable pageable, List<Long> ids, String keyword) {

        Page<User> users = userDeptRepository.findAllByDeptId(pageable, ids, keyword);

        return users.map(UserDeptResponse::from);
    }

    @Transactional
    public void changeUserDept(UserDeptRequest request) {
        Dept targetDept = request.targetDeptId() != null ?
                deptRepository.findById(request.targetDeptId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.DEPT_CODE_NOT_FOUND)) : null;

        List<Long> userIds = request.userDepts()
                .stream().
                map(UserDeptDto::userId)
                .toList();

        List<User> users = userRepository.findAllByIdIn(userIds);

        List<AuditLogUpdateRequest> updateRequests = new ArrayList<>();
        for (User user : users) {
            UserDept currentDept = user.getUserDept();
            UserDeptAuditLog before = (currentDept != null) ? UserDeptAuditLog.from(currentDept) : null;

            if (targetDept == null) {
                user.assignDepartment(null);
            } else {
                if (currentDept != null) {
                    currentDept.updateDept(targetDept);
                } else {
                    UserDept newUserDept = UserDept.builder()
                            .user(user)
                            .dept(targetDept)
                            .build();
                    user.assignDepartment(newUserDept);
                }
            }

            UserDeptAuditLog after = (user.getUserDept() != null) ? UserDeptAuditLog.from(user.getUserDept()) : null;
            updateRequests.add(new AuditLogUpdateRequest(
                    user.getId(),
                    user.getEmailId(),
                    before,
                    after
            ));

        }
        auditLogService.logUpdate(AuditTarget.USER_DEPT, updateRequests);
    }
}

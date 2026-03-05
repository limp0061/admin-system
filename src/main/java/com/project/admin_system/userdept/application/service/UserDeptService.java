package com.project.admin_system.userdept.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.logs.application.dto.AuditLogUpdateRequest;
import com.project.admin_system.logs.application.service.AuditLogService;
import com.project.admin_system.logs.domain.AuditTarget;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.userdept.application.dto.UserDeptAuditLog;
import com.project.admin_system.userdept.application.dto.UserDeptDto;
import com.project.admin_system.userdept.application.dto.UserDeptRequest;
import com.project.admin_system.userdept.application.dto.UserDeptResponse;
import com.project.admin_system.userdept.domain.UserDept;
import com.project.admin_system.userdept.domain.UserDeptRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDeptService {

    private final UserDeptRepository userDeptRepository;
    private final DeptRepository deptRepository;
    private final AuditLogService auditLogService;

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

        List<UserDept> allUserDepts = userDeptRepository.findAllByUserIdInWithAll(userIds);

        List<AuditLogUpdateRequest> updateRequests = new ArrayList<>();
        for (UserDeptDto dto : request.userDepts()) {
            Optional<UserDept> isExist = allUserDepts.stream()
                    .filter(userDept -> userDept.getUserId().equals(dto.userId())
                            && userDept.getDept().getId().equals(dto.deptId()))
                    .findFirst();

            if (isExist.isPresent()) {
                UserDept userDept = isExist.get();

                UserDeptAuditLog before = UserDeptAuditLog.from(userDept);
                if (targetDept == null) {
                    UserDeptAuditLog after = new UserDeptAuditLog(
                            userDept.getUserId(),
                            userDept.getUser().getName(),
                            null,
                            null
                    );
                    updateRequests.add(
                            new AuditLogUpdateRequest(userDept.getUserId(), userDept.getUser().getEmailId(), before,
                                    after));
                    userDept.getUser().assignDepartment(null);
                } else {
                    userDept.updateDept(targetDept);
                    UserDeptAuditLog after = UserDeptAuditLog.from(userDept);
                    updateRequests.add(
                            new AuditLogUpdateRequest(userDept.getUserId(), userDept.getUser().getEmailId(), before,
                                    after));
                }
            } else {
                throw new BusinessException(ErrorCode.USER_DEPT_NOT_FOUND);
            }
        }
        auditLogService.logUpdate(AuditTarget.USER_DEPT, updateRequests);
    }
}

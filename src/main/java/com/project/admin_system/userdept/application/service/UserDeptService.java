package com.project.admin_system.userdept.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.user.application.dto.UserListResponse;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.userdept.application.dto.UserDeptDto;
import com.project.admin_system.userdept.application.dto.UserDeptRequest;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.userdept.application.dto.UserDeptResponse;
import com.project.admin_system.userdept.domain.UserDept;
import com.project.admin_system.userdept.domain.UserDeptRepository;
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

        for (UserDeptDto dto : request.userDepts()) {
            Optional<UserDept> isExist = allUserDepts.stream()
                    .filter(userDept -> userDept.getUserId().equals(dto.userId())
                            && userDept.getDept().getId().equals(dto.deptId()))
                    .findFirst();

            if (isExist.isPresent()) {
                UserDept targetEntity = isExist.get();
                if (targetDept == null) {
                    targetEntity.getUser().assignDepartment(null);
                } else {
                    targetEntity.updateDept(targetDept);
                }
            } else {
                throw new BusinessException(ErrorCode.USER_DEPT_NOT_FOUND);
            }
        }
    }

}

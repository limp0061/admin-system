package com.project.admin_system.dept.application.validate;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.dept.application.dto.DeptSaveRequest;
import com.project.admin_system.dept.application.dto.DeptValidResponse;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.userdept.domain.UserDeptRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeptValidator {

    private final DeptRepository deptRepository;
    private final UserDeptRepository userDeptRepository;

    public Dept validateCreateAndGetUpperDept(DeptSaveRequest dto) {
        validateDuplicateCode(dto.deptCode(), null);
        return getUpperDeptIfExists(dto.upperDeptId());
    }

    public Dept validateUpdateAndGetUpperDept(DeptSaveRequest dto) {
        validateDuplicateCode(dto.deptCode(), dto.id());
        return getUpperDeptIfExists(dto.upperDeptId());
    }

    public void validateDuplicateCode(String deptCode, Long id) {
        deptRepository.findByDeptCode(deptCode).ifPresent(dept -> {
            if (!dept.getId().equals(id)) {
                throw new BusinessException(ErrorCode.DUPLICATE_DEPT_CODE);
            }
        });
    }

    public Dept getUpperDeptIfExists(Long upperDeptId) {
        if (upperDeptId == null || upperDeptId == 0L) {
            return null;
        }

        return deptRepository.findById(upperDeptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UPPER_DEPT_CODE_NOT_FOUND));
    }

    public DeptValidResponse validateForDelete(Long id) {

        Dept dept = deptRepository.findWithChildrenById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPT_CODE_NOT_FOUND));

        List<Dept> allChildren = new ArrayList<>();
        findAllChildrenRecursive(dept, allChildren);

        int childCount = allChildren.size();

        List<Long> deptIds = new ArrayList<>();
        deptIds.add(dept.getId());
        deptIds.addAll(allChildren.stream().map(Dept::getId).toList());

        long userCount = userDeptRepository.countActiveUsersInDepts(deptIds);

        boolean isDeletable = (userCount == 0);
        String message;
        if (!isDeletable) {
            message = String.format("해당 부서 및 하위 부서에 <b>%d명</b>의 사용자가<br/>소속되어 있어 삭제할 수 없습니다.", userCount);
        } else {
            if (childCount > 0) {
                message = String.format("정말로 <b>[%s]</b> 부서를 삭제하시겠습니까?<br/>삭제 시 하위 부서 %d개도 함께 삭제됩니다.",
                        dept.getDeptName(), childCount);
            } else {
                message = String.format("정말로 <b>[%s]</b> 부서를 삭제하시겠습니까?", dept.getDeptName());
            }
        }

        return new DeptValidResponse(isDeletable, message, dept);
    }

    private void findAllChildrenRecursive(Dept dept, List<Dept> totalList) {
        for (Dept child : dept.getChildren()) {
            totalList.add(child);
            findAllChildrenRecursive(child, totalList);
        }
    }

}

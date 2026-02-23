package com.project.admin_system.dept.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.dept.application.dto.DeptNode;
import com.project.admin_system.dept.application.dto.DeptSaveRequest;
import com.project.admin_system.dept.application.dto.DeptValidResponse;
import com.project.admin_system.dept.application.validate.DeptValidator;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.user.domain.UserRepository;
import com.project.admin_system.userdept.domain.UserDept;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeptService {

    private final DeptRepository deptRepository;
    private final DeptValidator deptValidator;
    private final UserRepository userRepository;

    @Transactional
    public void createDept(DeptSaveRequest dto) {

        Dept upperDept = deptValidator.validateCreateAndGetUpperDept(dto);
        int depth = (upperDept == null) ? 1 : upperDept.getDepth() + 1;

        deptRepository.save(dto.toEntity(depth, upperDept));
    }

    @Transactional
    public void updateDept(DeptSaveRequest dto) {
        Dept upperDept = deptValidator.validateUpdateAndGetUpperDept(dto);

        int depth = (upperDept == null) ? 1 : upperDept.getDepth() + 1;

        Dept dept = deptRepository.findById(dto.id())
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPT_CODE_NOT_FOUND));
        dept.update(dto, upperDept, depth);
    }

    public Dept findDeptById(Long id) {
        return deptRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPT_CODE_NOT_FOUND));
    }

    public Dept findWithChildren(Long id) {
        return deptRepository.findWithChildrenById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPT_CODE_NOT_FOUND));
    }

    @Transactional
    public void deleteById(Long deptId) {

        DeptValidResponse deptValidResponse = deptValidator.validateForDelete(deptId);
        if (!deptValidResponse.isDeletable()) {
            throw new BusinessException(ErrorCode.DEPT_CAN_NOT_DELETE);
        }

        Dept dept = deptRepository.findById(deptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPT_CODE_NOT_FOUND));

        deptRepository.delete(dept);
    }

    public List<DeptNode> selectDeptNodes() {
        List<DeptNode> tree = findDeptNodes();
        return flatten(tree);
    }

    private List<DeptNode> flatten(List<DeptNode> nodes) {
        List<DeptNode> selectNodes = new ArrayList<>();
        if (nodes == null || nodes.isEmpty()) {
            return selectNodes;
        }

        for (DeptNode node : nodes) {
            selectNodes.add(node);
            if (!node.getChildren().isEmpty()) {
                selectNodes.addAll(flatten(node.getChildren()));
            }
        }
        return selectNodes;
    }

    public List<DeptNode> findDeptNodes() {
        List<Dept> deptList = deptRepository.findAllOrderBySortOrder();
        int totalUserCount = (int) userRepository.countByActiveStatus();

        Map<Long, DeptNode> nodeMap = new LinkedHashMap<>();
        for (Dept dept : deptList) {
            nodeMap.put(dept.getId(), DeptNode.builder()
                    .id(dept.getId())
                    .deptCode(dept.getDeptCode())
                    .deptName(dept.getDeptName())
                    .upperDeptId(dept.getUpperDept() != null ? dept.getUpperDept().getId() : null)
                    .isActive(dept.isActive())
                    .sortOrder(dept.getSortOrder())
                    .depth(dept.getDepth())
                    .count((int) dept.getUserDepts().stream()
                            .map(UserDept::getUser)
                            .filter(u -> u != null && u.isManagedUser())
                            .count())
                    .hasChildren(false)
                    .build());
        }

        List<DeptNode> rootNodes = new ArrayList<>();
        for (Dept dept : deptList) {
            DeptNode current = nodeMap.get(dept.getId());
            Long upperId = (dept.getUpperDept() != null) ? dept.getUpperDept().getId() : null;

            if (upperId == null) {
                rootNodes.add(current);
            } else {
                DeptNode parent = nodeMap.get(upperId);
                if (parent != null) {
                    parent.getChildren().add(current);
                    parent.setHasChildren(true);
                }
            }
        }

        for (DeptNode root : rootNodes) {
            calculateTotalCountRecursive(root);
        }

        DeptNode rootNode = DeptNode.builder()
                .id(0L).deptName("전체").deptCode("ROOT").depth(0).isActive(true)
                .count(totalUserCount).hasChildren(!rootNodes.isEmpty())
                .build();
        rootNode.setChildren(rootNodes);

        return List.of(rootNode);
    }

    private int calculateTotalCountRecursive(DeptNode node) {
        int totalSum = node.getCount();
        for (DeptNode child : node.getChildren()) {
            totalSum += calculateTotalCountRecursive(child);
        }
        node.setCount(totalSum);
        return totalSum;
    }

    public List<Long> findAllSubDeptIds(Long targetDeptId) {
        List<Dept> allDepts = deptRepository.findAll();

        Map<Long, List<Long>> parentToChildrenMap = allDepts.stream()
                .filter(d -> d.getUpperDept() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getUpperDept().getId(),
                        Collectors.mapping(Dept::getId, Collectors.toList())
                ));

        List<Long> ids = new ArrayList<>();
        collectChildIdsRecursive(targetDeptId, parentToChildrenMap, ids);

        return ids;
    }

    private void collectChildIdsRecursive(Long parentId, Map<Long, List<Long>> map, List<Long> ids) {
        ids.add(parentId);
        List<Long> childrenIds = map.get(parentId);
        if (childrenIds != null) {
            for (Long childId : childrenIds) {
                collectChildIdsRecursive(childId, map, ids);
            }
        }
    }
}

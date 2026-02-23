package com.project.admin_system.resources.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.RedisEventPublisher;
import com.project.admin_system.resources.application.dto.ResourceRefreshEvent;
import com.project.admin_system.resources.application.dto.RoleDto;
import com.project.admin_system.resources.application.dto.RoleSaveRequest;
import com.project.admin_system.resources.application.dto.RoleTreeDto;
import com.project.admin_system.resources.application.dto.RoleValidResponse;
import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.resources.domain.RoleRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final RedisEventPublisher redisEventPublisher;
    private final RoleValidator roleValidator;

    public String generateHierarchy() {
        List<Role> roleHierarchies = roleRepository.findAll(Sort.by(Sort.Direction.ASC, "depth"));

        StringBuilder sb = new StringBuilder();
        for (Role role : roleHierarchies) {
            if (role.getParent() != null && !role.getRoleKey().equals("ROLE_ANONYMOUS")) {
                sb.append(role.getParent().getRoleKey())
                        .append(" > ")
                        .append(role.getRoleKey())
                        .append("\n");
            }

            if (role.getChildren().isEmpty() && !role.getRoleKey().equals("ROLE_ANONYMOUS")) {
                sb.append(role.getRoleKey())
                        .append(" > ROLE_ANONYMOUS\n");
            }
        }

        return sb.toString();
    }

    public List<RoleDto> findAllByAdminFilter(boolean isAdmin) {
        List<Role> roles = roleRepository.findAllByAdminFilter(isAdmin);

        return roles.stream().map(RoleDto::from).toList();
    }

    public List<Role> findAllByIds(List<Long> ids) {
        return roleRepository.findAllByIdIn(ids);
    }

    public List<RoleTreeDto> selectRoleTree() {
        List<RoleTreeDto> roleTree = getRoleTree();
        return flatten(roleTree);
    }

    private List<RoleTreeDto> flatten(List<RoleTreeDto> roleTree) {
        List<RoleTreeDto> selectNodes = new ArrayList<>();
        if (roleTree == null || roleTree.isEmpty()) {
            return roleTree;
        }

        for (RoleTreeDto roleTreeDto : roleTree) {
            selectNodes.add(roleTreeDto);

            if (!roleTreeDto.children().isEmpty()) {
                selectNodes.addAll(flatten(roleTreeDto.children()));
            }
        }

        return selectNodes;
    }

    public List<RoleTreeDto> getRoleTree() {
        List<Role> roles = roleRepository.findAllByRoleKeyNot("ROLE_ANONYMOUS");
        Map<Long, RoleTreeDto> dtoMap = new LinkedHashMap<>();
        for (Role role : roles) {
            dtoMap.put(role.getId(), new RoleTreeDto(
                    role.getId(),
                    role.getParent() != null ? role.getParent().getId() : null,
                    role.getRoleKey(),
                    role.getRoleName(),
                    role.getDepth(),
                    new ArrayList<>(),
                    role.isAdmin()
            ));
        }

        List<RoleTreeDto> rootNodes = new ArrayList<>();
        for (Role role : roles) {
            RoleTreeDto dto = dtoMap.get(role.getId());
            if (dto.parentId() == null) {
                rootNodes.add(dto);
            } else {
                RoleTreeDto parentDto = dtoMap.get(role.getParent().getId());
                if (parentDto != null) {
                    parentDto.children().add(dto);
                }
            }
        }

        return rootNodes;
    }

    public Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    @Transactional
    public void saveRole(RoleSaveRequest request) {

        Role parentRole = (request.parentId() != null)
                ? roleRepository.findById(request.parentId()).orElse(null)
                : null;
        Role role = request.toEntity(parentRole);

        roleRepository.save(role);

        ResourceRefreshEvent event = new ResourceRefreshEvent("ROLE", List.of(role.getId()), "CREATE");
        redisEventPublisher.refreshResource(event);
    }

    @Transactional
    public void updateRole(RoleSaveRequest request, Long id) {

        Role role = findRoleById(id);

        Role parentRole = (request.parentId() != null)
                ? roleRepository.findById(request.parentId()).orElse(null)
                : null;

        if (!role.getRoleKey().equals(request.roleKey())) {
            roleValidator.duplicateRoleCheck(request.roleKey());
        }
        role.update(request.roleKey(), request.roleName(), parentRole, request.isAdmin());

        ResourceRefreshEvent event = new ResourceRefreshEvent("ROLE", List.of(role.getId()), "UPDATE");
        redisEventPublisher.refreshResource(event);
    }

    @Transactional
    public void deleteRole(Long id) {
        RoleValidResponse roleValidResponse = roleValidator.validateForDelete(id);
        if (!roleValidResponse.isDeletable()) {
            throw new Error(roleValidResponse.message());
        }

        roleRepository.deleteById(id);
        ResourceRefreshEvent event = new ResourceRefreshEvent("ROLE", List.of(id), "DELETE");
        redisEventPublisher.refreshResource(event);
    }
}


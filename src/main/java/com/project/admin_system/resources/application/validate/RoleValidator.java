package com.project.admin_system.resources.application.validate;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.resources.application.dto.RoleDto;
import com.project.admin_system.resources.application.dto.RoleValidResponse;
import com.project.admin_system.resources.domain.ResourcesRepository;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.resources.domain.RoleRepository;
import com.project.admin_system.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleValidator {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ResourcesRepository resourcesRepository;

    public Role validateRole(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    public Role validateRoleKey(String roleKey) {
        return roleRepository.findByRoleKey(roleKey).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    public RoleValidResponse validateForDelete(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        boolean isDeletable = false;
        String message = "";
        if (!role.getChildren().isEmpty()) {
            message = ErrorCode.ROLE_HAS_CHILDREN.getMessage();

        } else if (userRepository.existsByRoleId(roleId)) {
            message = ErrorCode.ROLE_HAS_USERS.getMessage();
        } else if (resourcesRepository.existsByRoles_Id(roleId)) {
            message = ErrorCode.ROLE_HAS_URL_PATTERN.getMessage();
        } else {
            isDeletable = true;
        }

        return RoleValidResponse.of(isDeletable, RoleDto.from(role), message);
    }

    public void duplicateRoleCheck(String roleKey) {
        if (roleRepository.existsByRoleKey(roleKey)) {
            throw new BusinessException(ErrorCode.DUPLICATE_ROLE_KEY);
        }
    }
}

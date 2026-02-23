package com.project.admin_system.resources.application.dto;

import com.project.admin_system.resources.domain.Method;
import com.project.admin_system.resources.domain.Resource;
import com.project.admin_system.resources.domain.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ResourcesListResponse(
        Long id,
        String name,
        String urlPattern,
        Method method,
        List<RoleDto> roles,
        String displayRole,
        String description,
        LocalDateTime updatedAt
) {

    public static ResourcesListResponse from(Resource resource) {
        List<Role> roleEntities = resource.getRoles().stream()
                .toList();

        String displayRole = "전체 허용";

        if (!roleEntities.isEmpty()) {
            if (roleEntities.size() > 2) {
                displayRole = roleEntities.subList(0, 2).stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.joining(", "))
                        + " 외 " + (roleEntities.size() - 2) + "개";
            } else {
                displayRole = roleEntities.stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.joining(", "));
            }
        }

        List<RoleDto> roles = roleEntities.stream()
                .map(RoleDto::from)
                .toList();

        return new ResourcesListResponse(
                resource.getId(),
                resource.getName(),
                resource.getUrlPattern(),
                resource.getMethod(),
                roles,
                displayRole,
                resource.getDescription(),
                resource.getUpdatedAt()
        );
    }
}

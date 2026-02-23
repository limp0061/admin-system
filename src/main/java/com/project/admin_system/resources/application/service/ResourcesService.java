package com.project.admin_system.resources.application.service;

import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.common.service.RedisEventPublisher;
import com.project.admin_system.resources.application.dto.ResourceRefreshEvent;
import com.project.admin_system.resources.application.dto.ResourcesListResponse;
import com.project.admin_system.resources.application.dto.ResourcesSaveRequest;
import com.project.admin_system.resources.application.dto.ResourcesSearchRequest;
import com.project.admin_system.resources.application.validate.ResourcesValidator;
import com.project.admin_system.resources.domain.Resource;
import com.project.admin_system.resources.domain.ResourcesRepository;

import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Role;

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
public class ResourcesService {

    private final ResourcesRepository resourcesRepository;
    private final ResourcesValidator resourcesValidator;

    private final RoleService roleService;
    private final RoleValidator roleValidator;

    private final RedisEventPublisher redisEventPublisher;

    public Page<ResourcesListResponse> getAllResources(Pageable pageable, ResourcesSearchRequest request) {
        Page<Resource> resources = resourcesRepository.findAllOrderBySortOrder(pageable, request);

        return resources.map(ResourcesListResponse::from);
    }

    public ResourcesListResponse getResourceDetail(Long id) {
        return ResourcesListResponse.from(resourcesRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)));
    }

    @Transactional
    public void saveResource(ResourcesSaveRequest request) {
        Resource resource;

        List<Role> roles = roleService.findAllByIds(request.roleIds());

        if (roles.isEmpty()) {
            Role role = roleValidator.validateRoleKey("ROLE_ANONYMOUS");
            roles.add(role);
        }

        resourcesValidator.checkDuplicate(request);
        resource = Resource.builder()
                .name(request.name())
                .urlPattern(request.urlPattern())
                .method(request.method())
                .description(request.description())
                .build();

        resourcesRepository.save(resource);

        resource.addRoles(roles);
        ResourceRefreshEvent event = new ResourceRefreshEvent("RESOURCE", List.of(resource.getId()), "CREATE");
        redisEventPublisher.refreshResource(event);
    }

    @Transactional
    public void updateResource(ResourcesSaveRequest request, Long resourceId) {
        Resource resource;

        List<Role> roles = roleService.findAllByIds(request.roleIds());

        if (roles.isEmpty()) {
            Role role = roleValidator.validateRoleKey("ROLE_ANONYMOUS");
            roles.add(role);
        }

        resourcesValidator.checkDuplicateForUpdate(request);
        resource = resourcesRepository.findById(resourceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        resource.updateFields(request.name(), request.method(), request.description());
        resource.addRoles(roles);

        ResourceRefreshEvent event = new ResourceRefreshEvent("RESOURCE", List.of(resource.getId()), "UPDATE");
        redisEventPublisher.refreshResource(event);
    }


    public int countByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        int count = resourcesRepository.countByIdIn(ids);
        if (count == 0) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return count;
    }

    @Transactional
    public long deleteResource(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        resourcesRepository.deleteAllByIdInBatch(ids);

        ResourceRefreshEvent event = new ResourceRefreshEvent("RESOURCE", ids, "DELETE");
        redisEventPublisher.refreshResource(event);

        return ids.size();
    }
}

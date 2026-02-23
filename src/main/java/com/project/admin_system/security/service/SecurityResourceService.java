package com.project.admin_system.security.service;

import com.project.admin_system.security.dynamic.DynamicRoleHierarchy;
import com.project.admin_system.security.manager.DynamicAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecurityResourceService {

    private final DynamicRoleHierarchy dynamicRoleHierarchy;
    private final DynamicAuthorizationManager dynamicAuthorizationManager;

    @Transactional
    public void refreshResource() {
        dynamicRoleHierarchy.reload();
        dynamicAuthorizationManager.reload();
    }
}

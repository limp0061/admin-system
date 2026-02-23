package com.project.admin_system.security.dynamic;

import com.project.admin_system.resources.application.service.RoleService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRoleHierarchy implements RoleHierarchy {

    private final RoleService roleHierarchyService;
    private RoleHierarchy roleHierarchy;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        reload();
    }

    public void reload() {
        String hierarchy = roleHierarchyService.generateHierarchy();
        log.info("Generated Hierarchy: \n{}", hierarchy);
        this.roleHierarchy = RoleHierarchyImpl.fromHierarchy(hierarchy);
    }

    @Override
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return roleHierarchy.getReachableGrantedAuthorities(authorities);
    }
}

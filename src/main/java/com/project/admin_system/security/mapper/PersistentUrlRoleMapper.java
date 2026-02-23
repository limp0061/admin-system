package com.project.admin_system.security.mapper;

import com.project.admin_system.resources.domain.Method;
import com.project.admin_system.resources.domain.Resource;
import com.project.admin_system.resources.domain.ResourcesRepository;
import com.project.admin_system.security.dynamic.ResourceRule;
import com.project.admin_system.resources.domain.Role;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
public class PersistentUrlRoleMapper {

    private final ResourcesRepository resourcesRepository;

    private final RoleHierarchy roleHierarchy;

    public PersistentUrlRoleMapper(ResourcesRepository resourcesRepository, RoleHierarchy roleHierarchy) {
        this.resourcesRepository = resourcesRepository;
        this.roleHierarchy = roleHierarchy;
    }

    /*
    1. /** 맨 아래
    2. 와일드카드 적은 것 우선
    3. URL 길이 긴 것 우선
    4. method ALL 맨 아래
        /admin/user/1
        /admin/user/*
        /admin/user/**
        /admin/**
        /**
     */
    public List<ResourceRule> loadRules() {
        List<Resource> resources = resourcesRepository.findAllWithRoles();

        return resources.stream()
                .sorted(Comparator
                        .comparing((Resource resource) -> resource.getUrlPattern().contains("**")) // ** 맨 뒤로
                        .thenComparingInt(resource -> countAsterisk(resource.getUrlPattern(), '*')) // * 갯수가 적은거 우선
                        .thenComparing(Comparator.comparingInt((Resource resource) -> resource.getUrlPattern().length())
                                .reversed()) // 길이가 긴거 우선
                        .thenComparingInt(resource -> resource.getMethod().equals(Method.ALL) ? 1 : 0) // ALL 이 아닌것 우선
                )
                .map(resource -> {
                    String[] roles = resource.getRoles().stream()
                            .map(Role::getRoleKey).toArray(String[]::new);

                    if (roles.length == 0) {
                        AuthorizationManager<RequestAuthorizationContext> permitAllManager =
                                (authentication, context) -> new AuthorizationDecision(true);

                        return new ResourceRule(
                                resource.getUrlPattern(),
                                resource.getMethod(),
                                permitAllManager
                        );
                    }

                    // 타입이 긴 경우 var 를 사용하면 타입을 추론
                    // AuthorityAuthorizationManager 는 이 사용자가 이 URL 에 자격(role)이 있는지
                    // 요청이 들어올 때마다 RequestAuthorizationContext 를 생성해서 매니저한테 던짐
                    // hasAnyAuthority 는 문자열 그대로 비교하고 hasAnyRole 는 ROLE을 붙힘
                    var manager = AuthorityAuthorizationManager.<RequestAuthorizationContext>hasAnyAuthority(
                            roles);
                    manager.setRoleHierarchy(roleHierarchy);

                    return new ResourceRule(
                            resource.getUrlPattern(),
                            resource.getMethod(),
                            manager
                    );
                }).toList();
    }

    private int countAsterisk(String str, char c) {
        return (int) str.chars().filter(ch -> ch == c).count();
    }
}

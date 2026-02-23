package com.project.admin_system.security.manager;

import com.project.admin_system.security.dynamic.ResourceRule;
import com.project.admin_system.security.mapper.PersistentUrlRoleMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final PersistentUrlRoleMapper roleMapper;

    private List<ResourceRule> rules;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        reload();
    }

    public void reload() {
        this.rules = roleMapper.loadRules();
    }

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        AuthorizationManager.super.verify(authentication, context);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {

        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return new AuthorizationDecision(false);
        }

        boolean isSuper = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER"));

        if (isSuper) {
            return new AuthorizationDecision(true);
        }

        HttpServletRequest request = context.getRequest();

        for (ResourceRule rule : rules) {
            if (rule.matches(request)) {
                return rule.authorize(authentication, context);
            }
        }

        return new AuthorizationDecision(false);
    }

    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        return AuthorizationManager.super.authorize(authentication, context);
    }
}

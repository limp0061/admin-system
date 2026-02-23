package com.project.admin_system.security.dynamic;

import com.project.admin_system.resources.domain.Method;
import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Supplier;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class ResourceRule {

    private final PathPattern pathPattern;

    private final HttpMethod httpMethod;

    private final AuthorizationManager<RequestAuthorizationContext> manager;

    public ResourceRule(String urlPattern, Method method,
                        AuthorizationManager<RequestAuthorizationContext> manager) {

        this.pathPattern = new PathPatternParser().parse(urlPattern);
        this.httpMethod = "ALL".equals(method) ? null : HttpMethod.valueOf(method.name());
        this.manager = manager;
    }

    public boolean matches(HttpServletRequest request) {
        if (httpMethod != null && httpMethod != HttpMethod.valueOf(request.getMethod())) {
            return false;
        }
        return pathPattern.matches(PathContainer.parsePath(request.getRequestURI()));
    }

    public AuthorizationDecision authorize(Supplier<Authentication> authentication,
                                           RequestAuthorizationContext context) {
        return (AuthorizationDecision) manager.authorize(authentication, context);
    }
}

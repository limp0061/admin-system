package com.project.admin_system.common.config;

import com.project.admin_system.security.handler.LoginFailureHandler;
import com.project.admin_system.security.handler.LoginSuccessHandler;
import com.project.admin_system.security.manager.DynamicAuthorizationManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@EnableWebSecurity
@RequiredArgsConstructor
@Component
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;

    // 정적 리소스 제외
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations()) // 기본 정적 리소스(css, js, images 등) 일괄 제외
                .requestMatchers("/h2-console/**", "/docs/**", "/swagger-ui/**", "/swagger-ui.html", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DynamicAuthorizationManager dynamicAuthorizationManager)
            throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/api/login", "/logout").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .anyRequest().access(dynamicAuthorizationManager)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/login") // true : 어떤 경로로 들어오든 로그인 성공 시 이동
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                )
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            String requestedWith = request.getHeader("X-Requested-With");
                            String uri = request.getRequestURI();
                            boolean isAjax = "XMLHttpRequest".equals(requestedWith) || request.getRequestURI()
                                    .startsWith("/api/")
                                    || uri.contains("/modal/");

                            if (isAjax) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"status\":403, \"message\":\"해당 기능에 대한 권한이 없습니다.\"}");
                            } else {
                                response.sendRedirect("/error/403");
                            }
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().write("{\"status\":401, \"message\":\"로그인이 필요합니다.\"}");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                );
        return http.build();
    }
}

package com.project.admin_system.resources.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.admin_system.common.annotation.IntegrationTest;
import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.resources.application.dto.ResourcesSaveRequest;
import com.project.admin_system.resources.domain.Method;
import com.project.admin_system.resources.domain.Resource;
import com.project.admin_system.resources.domain.ResourcesRepository;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.resources.domain.RoleRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@IntegrationTest
@Transactional
class ResourcesServiceIntegrationTest {

    @Autowired
    private ResourcesService resourcesService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourcesRepository resourcesRepository;


    @Autowired
    private EntityManager em;

    private Long savedRoleId;
    private Long savedResourceId;

    @BeforeEach
    void init() {
        Role role = roleRepository.findByRoleKey("ROLE_TEST")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleKey("ROLE_TEST")
                                .roleName("관리자")
                                .isAdmin(true)
                                .build()
                ));
        savedRoleId = role.getId();

        Resource resource = resourcesRepository.findByUrlPatternAndMethod("/users/test/**", Method.POST)
                .orElseGet(() -> resourcesRepository.save(Resource.builder()
                        .name("사용자 페이지")
                        .urlPattern("/users/test/**")
                        .method(Method.POST)
                        .description("사용자 페이지 접근 정책")
                        .build())
                );
        savedResourceId = resource.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("리스소 추가 성공")
    void createResource_success() {

        // given
        String urlPattern = "/admins/test/**";
        ResourcesSaveRequest requestDto = new ResourcesSaveRequest(
                null, "관리자 페이지 테스트", urlPattern,
                Method.GET, List.of(savedRoleId), "관리자 페이지 접근 정책"
        );

        // when
        resourcesService.saveResource(requestDto);

        em.flush();
        em.clear();

        // then
        Resource resource = resourcesRepository.findByUrlPatternAndMethod(urlPattern, Method.GET).orElse(null);
        assertThat(resource).isNotNull();
        assertThat(resource.getUrlPattern()).isEqualTo(urlPattern);
    }

    @Test
    @DisplayName("리소스 수정 성공")
    void updateResource_success() {
        // given
        String urlName = "관리자 페이지 전체 경로";
        ResourcesSaveRequest requestDto = new ResourcesSaveRequest(
                savedResourceId, urlName, "/admins/test/**",
                Method.GET, List.of(savedRoleId), "관리자 페이지 접근 정책"
        );

        // when
        resourcesService.updateResource(requestDto, savedResourceId);

        em.flush();
        em.clear();

        // then
        Resource newResource = resourcesRepository.findById(savedResourceId).orElseThrow();
        assertThat(newResource.getName()).isEqualTo(urlName);
        assertThat(newResource.getMethod()).isEqualTo(Method.GET);
    }

    @Test
    @DisplayName("리소스 추가 중복 체크")
    void deleteResource_fail_duplicate() {

        // given
        String urlPattern = "/users/test/**";
        ResourcesSaveRequest requestDto = new ResourcesSaveRequest(
                null, "사용자 페이지", urlPattern,
                Method.POST, List.of(savedRoleId), "사용자 페이지 접근 정책"
        );

        //when

        // then
        assertThatThrownBy(() -> resourcesService.saveResource(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_URL_PATTERN.getMessage());
    }

    @Test
    @DisplayName("리소스 삭제")
    void deleteResource_success() {

        // given
        // when
        resourcesService.deleteResource(List.of(savedResourceId));

        // then
        assertThat(resourcesRepository.findById(savedResourceId)).isEmpty();
    }
}
package com.project.admin_system.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.admin_system.common.annotation.IntegrationTest;
import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.resources.domain.RoleRepository;
import com.project.admin_system.user.application.dto.AdminRoleRequest;
import com.project.admin_system.user.domain.Gender;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserRepository;
import com.project.admin_system.user.domain.UserStatus;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class AdminUserServiceIntegrationTest {

    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private UserRepository userRepository;

    private Long adminUserId;

    @BeforeEach
    void init() {

        Role userRole = roleRepository.findByRoleKey("ROLE_USER")
                .orElseGet(() -> Role.builder()
                        .roleKey("ROLE_USER")
                        .roleName("사용자")
                        .depth(0)
                        .parent(null)
                        .build());

        roleRepository.save(userRole);

        User user = userRepository.findByEmailId("example@example.com")
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .emailId("example@example.com")
                                .name("테스트")
                                .password("1234")
                                .position("직원")
                                .userCode("USER001")
                                .gender(Gender.MALE)
                                .userStatus(UserStatus.ACTIVE)
                                .profilePath(null)
                                .role(userRole)
                                .build()
                ));
        user.addIps(List.of("0.0.0.0", "192.168.1.0"));

        adminUserId = user.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("관리자 추가 성공")
    void createAdminUser_success() {

        // given
        Role adminRole = roleRepository.findByRoleKey("ROLE_ADMIN")
                .orElseGet(() -> Role.builder()
                        .roleKey("ROLE_ADMIN")
                        .roleName("일반관리자")
                        .depth(0)
                        .parent(null)
                        .isAdmin(true)
                        .build());

        roleRepository.save(adminRole);

        AdminRoleRequest adminRoleRequest = new AdminRoleRequest(
                adminUserId,
                adminRole.getId(),
                List.of("0.0.0.1", "192.168.1.2", "211.251.254.210")
        );

        // when
        adminUserService.saveAdmin(adminRoleRequest);

        em.flush();
        em.clear();

        // then
        User admin = userRepository.findById(adminUserId).orElseThrow();
        assertThat(admin.getRole().getRoleKey()).isEqualTo("ROLE_ADMIN");
        assertThat(admin.getAllowedIps()).hasSize(3);
    }

    @Test
    @DisplayName("관리자 수정 성공")
    void updateAdminUser_success() {

        // given
        Role adminRole = roleRepository.findByRoleKey("ROLE_SUPER_TEST")
                .orElseGet(() -> Role.builder()
                        .roleKey("ROLE_SUPER_TEST")
                        .roleName("슈퍼관리자 테스트")
                        .depth(0)
                        .parent(null)
                        .isAdmin(true)
                        .build());

        roleRepository.save(adminRole);

        em.flush();
        em.clear();

        AdminRoleRequest adminRoleRequest = new AdminRoleRequest(
                adminUserId,
                adminRole.getId(),
                List.of("0.0.0.0", "192.168.1.0")
        );

        // when
        adminUserService.updateAdmin(adminUserId, adminRoleRequest);

        em.flush();
        em.clear();

        // then
        User admin = userRepository.findById(adminUserId).orElseThrow();
        assertThat(admin.getRole().getRoleKey()).isEqualTo("ROLE_SUPER_TEST");
        assertThat(admin.getAllowedIps()).hasSize(2);
    }

    @Test
    @DisplayName("관리자 수정 실패 : ip 형식 오류")
    void updateAdminUser_fail_invalid_ip_format() {
        // given
        AdminRoleRequest adminRoleRequest = new AdminRoleRequest(
                adminUserId,
                9999L,
                List.of("0.0.0.0", "192.168.1.0.0")
        );

        // when
        // then
        assertThatThrownBy(() -> adminUserService.updateAdmin(adminUserId, adminRoleRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_IP_FORMAT.getMessage());
    }

    @Test
    @DisplayName("관리자 삭제 성공")
    void deleteAdminUser_success() {

        // given
        // when
        adminUserService.deleteAdmin(List.of(adminUserId));

        em.flush();
        em.clear();

        //then
        User admin = userRepository.findById(adminUserId).orElseThrow();
        assertThat(admin.getRole().getRoleKey()).isEqualTo("ROLE_USER");
        assertThat(admin.getAllowedIps()).hasSize(0);
    }
}
package com.project.admin_system.resources.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.admin_system.common.annotation.IntegrationTest;
import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.resources.application.dto.RoleSaveRequest;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.resources.domain.RoleRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class RoleServiceIntegrationTest {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EntityManager em;

    private Long savedRoleId;

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
    }

    @Test
    @DisplayName("권한 추가 성공")
    void createRole_success() throws Exception {

        // given
        String roleKey = "ROLE_TEST_A";
        String roleName = "테스트 관리자";
        RoleSaveRequest roleSaveRequest = new RoleSaveRequest(null, roleKey, roleName, savedRoleId, true);

        // when
        roleService.saveRole(roleSaveRequest);

        em.flush();
        em.clear();

        // then
        Role role = roleRepository.findByRoleKey(roleKey).orElseThrow();
        assertThat(role.getRoleKey()).isEqualTo(roleKey);
        assertThat(role.getRoleName()).isEqualTo(roleName);

        Role upperRole = roleRepository.findById(savedRoleId).orElseThrow();
        assertThat(upperRole.getRoleKey()).isEqualTo("ROLE_TEST");
        assertThat(upperRole.getRoleName()).isEqualTo("관리자");
    }

    @Test
    @DisplayName("권한 수정 성공")
    void updateRole_success() throws Exception {

        String roleKey = "ROLE_TEST_A";
        String roleName = "테스트 관리자";
        RoleSaveRequest roleSaveRequest = new RoleSaveRequest(savedRoleId, roleKey, roleName, null, true);

        // when
        roleService.updateRole(roleSaveRequest, savedRoleId);

        em.flush();
        em.clear();

        // then
        Role role = roleRepository.findByRoleKey(roleKey).orElseThrow();
        assertThat(role.getRoleKey()).isEqualTo(roleKey);
        assertThat(role.getRoleName()).isEqualTo(roleName);
    }


    @Test
    @DisplayName("권한 수정 중복")
    void updateRole_fail_duplicate() throws Exception {

        // given
        Role role = roleRepository.save(
                Role.builder()
                        .roleKey("ROLE_TEST_2")
                        .roleName("관리자2")
                        .isAdmin(true)
                        .build()
        );

        String roleKey = "ROLE_TEST";
        String roleName = "테스트 관리자";
        RoleSaveRequest roleSaveRequest = new RoleSaveRequest(role.getId(), roleKey, roleName, null, true);

        // when
        // then
        assertThatThrownBy(() -> roleService.updateRole(roleSaveRequest, role.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_ROLE_KEY.getMessage());

    }

    @Test
    @DisplayName("권한 삭제 성공")
    public void deleteRole_success() throws Exception {

        // given
        // when
        roleService.deleteRole(savedRoleId);

        // then
        assertThat(roleRepository.findById(savedRoleId)).isEmpty();
    }

    @Test
    @DisplayName("권한 삭제 실패 : 하위 권한이 존재")
    public void deleteRole_fail() throws Exception {

        // given
        Role parent = roleRepository.findById(savedRoleId).orElseThrow();

        roleRepository.save(
                Role.builder()
                        .roleKey("ROLE_TEST_2")
                        .roleName("관리자2")
                        .parent(parent)
                        .isAdmin(true)
                        .build()
        );

        em.flush();
        em.clear();

        // when
        // then
        assertThatThrownBy(() -> roleService.deleteRole(savedRoleId))
                .isInstanceOf(Error.class)
                .hasMessage(ErrorCode.ROLE_HAS_CHILDREN.getMessage());
    }
}
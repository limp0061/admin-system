package com.project.admin_system.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.admin_system.common.annotation.IntegrationTest;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.logs.application.service.AuditLogService;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.resources.domain.RoleRepository;
import com.project.admin_system.user.application.dto.UserCreateRequest;
import com.project.admin_system.user.application.dto.UserStatusChangeRequest;
import com.project.admin_system.user.application.dto.UserUpdateRequest;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EntityManager em;

    @MockitoBean
    private AuditLogService auditLogService;

    private Long savedUserId;
    private Long approveUserId;
    private Long savedRoleId;
    private Long savedDeptId;

    @BeforeEach
    void init() {
        Role role = roleRepository.save(Role.builder()
                .roleKey("ROLE_ADMIN_TEST")
                .roleName("일반관리자")
                .depth(0)
                .parent(null)
                .isAdmin(true)
                .build()
        );

        savedRoleId = role.getId();

        Dept dept = deptRepository.save(
                Dept.builder()
                        .deptCode("DEPT_TEST_001")
                        .deptName("개발팀")
                        .upperDept(null)
                        .sortOrder(1)
                        .isActive(true)
                        .depth(0)
                        .build()
        );
        savedDeptId = dept.getId();

        User user = userRepository.save(User.builder()
                .emailId("example@example.com")
                .name("테스트")
                .password("1234")
                .position("직원")
                .userCode("USER001")
                .gender(Gender.MALE)
                .userStatus(UserStatus.ACTIVE)
                .profilePath(null)
                .role(role)
                .build()
        );

        savedUserId = user.getId();

        User user2 = userRepository.save(
                User.builder()
                        .emailId("example2@example.com")
                        .name("테스트2")
                        .password("1234")
                        .position("직원")
                        .userCode("USER002")
                        .gender(Gender.MALE)
                        .userStatus(UserStatus.INACTIVE)
                        .profilePath(null)
                        .role(role)
                        .build());

        approveUserId = user2.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("사용자 계정 추가")
    void createUser_success() {

        // given
        String unique = String.valueOf(System.currentTimeMillis()).substring(8);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        String emailId = "test" + unique + "@example.com";
        UserCreateRequest createRequest = new UserCreateRequest(
                "테스트", "1234", emailId,
                savedDeptId, "직원", "USER" + unique, Gender.MALE,
                UserStatus.ACTIVE, null, savedRoleId
        );

        //when
        userService.createUser(request, createRequest, null);

        //then
        User savedUser = userRepository.findByEmailId(emailId).orElseThrow();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmailId()).isEqualTo(createRequest.emailId());
        assertThat(passwordEncoder.matches(createRequest.password(), savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("사용자 계정 수정")
    void updateUser_success() {

        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        String unique = String.valueOf(System.currentTimeMillis()).substring(8);
        String emailId = "test" + unique + "@example.com";
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "테스트2", "12345", emailId,
                savedDeptId, "", "USER" + unique, Gender.MALE,
                UserStatus.ACTIVE, savedRoleId);

        //when
        userService.updateUser(request, savedUserId, updateRequest, null);

        //then
        User savedUser = userRepository.findByEmailId(emailId).orElseThrow();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmailId()).isEqualTo(updateRequest.emailId());
        assertThat(passwordEncoder.matches(updateRequest.password(), savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("사용자 계정 상태 변경 : 승인")
    void changeUserStatus_success_approve() {

        //given
        UserStatusChangeRequest request = new UserStatusChangeRequest(
                List.of(approveUserId), "APPROVE"
        );

        //when
        userService.updateUserStatus(request);

        //then
        User savedUser = userRepository.findById(approveUserId).orElseThrow();
        assertThat(savedUser.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("사용자 계정 상태 변경 : 삭제")
    void changeUserStatus_success_delete() {
        //given
        UserStatusChangeRequest request = new UserStatusChangeRequest(
                List.of(savedUserId), "DELETED"
        );

        //when
        userService.updateUserStatus(request);

        //then
        User savedUser = userRepository.findById(savedUserId).orElseThrow();
        assertThat(savedUser.isDeleted()).isTrue();
    }
}

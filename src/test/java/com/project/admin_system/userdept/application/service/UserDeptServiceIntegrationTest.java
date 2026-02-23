package com.project.admin_system.userdept.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.admin_system.common.annotation.IntegrationTest;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.resources.domain.Role;
import com.project.admin_system.resources.domain.RoleRepository;
import com.project.admin_system.user.domain.Gender;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserRepository;
import com.project.admin_system.user.domain.UserStatus;
import com.project.admin_system.userdept.application.dto.UserDeptDto;
import com.project.admin_system.userdept.application.dto.UserDeptRequest;
import com.project.admin_system.userdept.domain.UserDept;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class UserDeptServiceIntegrationTest {

    @Autowired
    private UserDeptService userDeptService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EntityManager em;

    private Long savedUserId;
    private Long savedDeptId;

    @BeforeEach
    void init() {
        Role role = roleRepository.findByRoleKey("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleKey("ROLE_ADMIN")
                                .roleName("일반관리자")
                                .depth(0)
                                .parent(null)
                                .isAdmin(true)
                                .build()

                ));

        Dept dept = deptRepository.findByDeptCode("TEST-DEPT-001")
                .orElseGet(() -> deptRepository.save(
                        Dept.builder()
                                .deptCode("TEST-DEPT-001")
                                .deptName("개발팀")
                                .upperDept(null)
                                .sortOrder(1)
                                .isActive(true)
                                .depth(0)
                                .build()
                ));
        savedDeptId = dept.getId();

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
                                .role(role)
                                .build()
                ));
        savedUserId = user.getId();
        UserDept userDept = UserDept.builder()
                .dept(dept)
                .build();
        user.assignDepartment(userDept);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("사용자 부서정보 변경 성공")
    void change_userDept_success() throws Exception {

        // given

        Dept dept = deptRepository.findByDeptCode("TEST-DEPT-002")
                .orElseGet(() -> deptRepository.save(
                        Dept.builder()
                                .deptCode("TEST-DEPT-002")
                                .deptName("인사팀")
                                .upperDept(null)
                                .sortOrder(1)
                                .isActive(true)
                                .depth(0)
                                .build()
                ));
        deptRepository.save(dept);

        UserDeptDto userDeptDto = new UserDeptDto(savedUserId, savedDeptId);
        UserDeptRequest userDeptRequest = new UserDeptRequest(dept.getId(), List.of(userDeptDto), "EDIT");

        // when
        userDeptService.changeUserDept(userDeptRequest);

        // then
        User savedUser = userRepository.findWithDeptById(savedUserId).orElseThrow();
        assertThat(savedUser.getUserDept().getDept().getId()).isEqualTo(dept.getId());
        assertThat(savedUser.getUserDept().getDept().getDeptName()).isEqualTo("인사팀");
    }
}
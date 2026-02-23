package com.project.admin_system.dept.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.admin_system.common.annotation.IntegrationTest;
import com.project.admin_system.common.exception.BusinessException;
import com.project.admin_system.common.exception.ErrorCode;
import com.project.admin_system.dept.application.dto.DeptSaveRequest;
import com.project.admin_system.dept.application.service.DeptService;
import com.project.admin_system.dept.domain.Dept;
import com.project.admin_system.dept.domain.DeptRepository;
import com.project.admin_system.user.domain.Gender;
import com.project.admin_system.user.domain.User;
import com.project.admin_system.user.domain.UserRepository;
import com.project.admin_system.user.domain.UserStatus;
import com.project.admin_system.userdept.domain.UserDept;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class DeptServiceIntegrationTest {

    @Autowired
    private DeptService deptService;
    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    private Long savedDeptId;
    private Long upperDeptId;

    @BeforeEach
    void init() {
        Dept dept1 = deptRepository.findByDeptCode("TEST-DEPT-001")
                .orElseGet(() -> deptRepository.save(
                        Dept.builder()
                                .deptCode("TEST-DEPT-001")
                                .deptName("상위부서")
                                .upperDept(null)
                                .sortOrder(0)
                                .isActive(true)
                                .depth(0)
                                .build()
                ));

        upperDeptId = dept1.getId();
        Dept dept2 = deptRepository.findByDeptCode("TEST-DEPT-002")
                .orElseGet(() -> deptRepository.save(
                        Dept.builder()
                                .deptCode("TEST-DEPT-002")
                                .deptName("하위부서")
                                .upperDept(dept1)
                                .sortOrder(0)
                                .isActive(true)
                                .depth(1)
                                .build()
                ));

        savedDeptId = dept2.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("부서 생성 성공")
    public void createDept_success() throws Exception {
        //given
        DeptSaveRequest request = new DeptSaveRequest(
                null, "신규부서", "TEST-DEPT-003",
                upperDeptId, 0, true
        );
        //when
        deptService.createDept(request);

        //then
        Dept dept = deptRepository.findByDeptCode("TEST-DEPT-003").orElseThrow();
        Dept upperDept = deptRepository.findById(upperDeptId).orElseThrow();
        assertThat(dept.getDeptName()).isEqualTo("신규부서");
        assertThat(dept.getDeptCode()).isEqualTo("TEST-DEPT-003");
        assertThat(dept.getDepth()).isEqualTo(upperDept.getDepth() + 1);
        assertThat(upperDept.getDeptName()).isEqualTo("상위부서");
    }

    @Test
    @DisplayName("부서 수정 성공")
    public void updateDept_success() throws Exception {
        //given
        DeptSaveRequest request = new DeptSaveRequest(
                savedDeptId, "수정된부서명", "UPDATED-001",
                upperDeptId, 0, true
        );
        //when
        deptService.updateDept(request);

        //then
        Dept dept = deptRepository.findById(savedDeptId).orElseThrow();

        Dept upperDept = deptRepository.findById(upperDeptId).orElseThrow();
        assertThat(dept.getDeptName()).isEqualTo("수정된부서명");
        assertThat(dept.getDeptCode()).isEqualTo("UPDATED-001");
        assertThat(dept.getDepth()).isEqualTo(upperDept.getDepth() + 1);
        assertThat(upperDept.getDeptName()).isEqualTo("상위부서");
    }

    @Test
    @DisplayName("부서 삭제 성공")
    public void deleteDept_success() throws Exception {
        //given

        //when
        deptService.deleteById(upperDeptId);

        //then
        assertThat(deptRepository.findById(upperDeptId)).isEmpty();
        assertThat(deptRepository.findById(savedDeptId)).isEmpty();
    }

    @Test
    @DisplayName("부서 삭제 실패: 소속 사용자가 있는 경우")
    public void deleteDept_fail_user_exists() throws Exception {

        // given
        User user = userRepository.save(
                User.builder()
                        .emailId("example-test@example.com")
                        .name("테스트")
                        .password("1234")
                        .position("직원")
                        .userCode("TEST-USER-001")
                        .gender(Gender.MALE)
                        .userStatus(UserStatus.ACTIVE)
                        .profilePath(null)
                        .role(null)
                        .build()
        );

        Dept dept = deptRepository.findById(savedDeptId).orElseThrow();
        UserDept userDept = UserDept.builder()
                .dept(dept)
                .build();
        user.assignDepartment(userDept);

        assertThatThrownBy(() -> deptService.deleteById(savedDeptId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DEPT_CAN_NOT_DELETE.getMessage());
    }

}

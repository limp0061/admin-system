package com.project.admin_system.user.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.project.admin_system.common.util.ApiDocumentUtils.SUCCESS_RESPONSE_FIELDS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.admin_system.user.application.dto.UserCreateRequest;
import com.project.admin_system.user.application.dto.UserStatusChangeRequest;
import com.project.admin_system.user.application.dto.UserUpdateRequest;
import com.project.admin_system.user.application.service.UserService;
import com.project.admin_system.user.application.validate.UserValidator;
import com.project.admin_system.user.domain.Gender;
import com.project.admin_system.user.domain.UserStatus;
import com.project.admin_system.user.domain.UserStatusMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserApiController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureRestDocs
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserValidator userValidator;

    @Test
    @DisplayName("사용자 추가 테스트 및 문서화")
    void saveUserTest() throws Exception {
        UserCreateRequest request = new UserCreateRequest("홍길동", "test1234!", "example@exmple.com", 1L,
                "부장", "1234", Gender.MALE, UserStatus.ACTIVE, "", null);

        MockMultipartFile userData = new MockMultipartFile(
                "userData",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/users")
                        .file(userData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("user-create",
                        resource(ResourceSnippetParameters.builder()
                                .tag("User API")
                                .summary("사용자 추가")
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build()),
                        requestPartFields("userData",
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("emailId").description("이메일"),
                                fieldWithPath("deptId").description("부서 ID").optional(),
                                fieldWithPath("position").description("직급").optional(),
                                fieldWithPath("userCode").description("사번").optional(),
                                fieldWithPath("gender").description("성별"),
                                fieldWithPath("userStatus").description("사용자 계정 상태"),
                                fieldWithPath("profilePath").description("프로필 이미지 경로").optional(),
                                fieldWithPath("roleId").description("권한(Role) ID").optional()
                        )
                ));
    }

    @Test
    @DisplayName("사용자 수정 테스트 및 문서화")
    void updateUserTest() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest("김이박", "test1234!@", "example2@example.com", 1L,
                "부장", "1234", Gender.FEMALE, UserStatus.DELETED, null);

        MockMultipartFile userData = new MockMultipartFile(
                "userData", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/users/{id}/update", 1L)
                        .file(userData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andExpect(status().isOk())
                .andDo(document("user-update", resource(ResourceSnippetParameters.builder()
                                .tag("User API")
                                .summary("사용자 수정")
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build()),
                        requestPartFields("userData",
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("emailId").description("이메일"),
                                fieldWithPath("deptId").description("부서 ID").optional(),
                                fieldWithPath("position").description("직급").optional(),
                                fieldWithPath("userCode").description("사번").optional(),
                                fieldWithPath("gender").description("성별"),
                                fieldWithPath("userStatus").description("사용자 계정 상태"),
                                fieldWithPath("roleId").description("권한(Role) ID").optional()
                        )
                ));
    }

    @Test
    @DisplayName("상태 변경 테스트")
    void changeStatusTest() throws Exception {
        List<Long> ids = List.of(1L, 2L, 3L);
        String mode = UserStatusMode.APPROVE.name();

        UserStatusChangeRequest request = new UserStatusChangeRequest(ids, mode);
        mockMvc.perform(post("/api/v1/users/changeStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("user-change-status",
                        resource(ResourceSnippetParameters.builder()
                                .tag("User API")
                                .summary("계정 상태 변경")
                                .requestFields(
                                        fieldWithPath("ids").description("계정 상태를 변경할 사용자의 ID"),
                                        fieldWithPath("mode").description("변경하고자 하는 계정 상태")
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }
}
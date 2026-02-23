package com.project.admin_system.resources.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.project.admin_system.common.util.ApiDocumentUtils.SUCCESS_RESPONSE_FIELDS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.admin_system.resources.application.dto.ResourcesSaveRequest;
import com.project.admin_system.resources.application.dto.RoleSaveRequest;
import com.project.admin_system.resources.application.service.ResourcesService;
import com.project.admin_system.resources.application.service.RoleService;
import com.project.admin_system.resources.application.validate.RoleValidator;
import com.project.admin_system.resources.domain.Method;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ResourcesApiController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
class ResourcesApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ResourcesService resourcesService;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private RoleValidator roleValidator;

    @Test
    @DisplayName("리소스 추가 및 문서화")
    void saveResources() throws Exception {
        ResourcesSaveRequest request = new ResourcesSaveRequest(
                null, "관리자 접근", "/admins", Method.GET, List.of(1L),
                "관리자 페이지에 접근을 허용 하는 정책");

        mockMvc.perform(post("/api/v1/resources/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("resource-save",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Resource API")
                                .summary("리소스 추가")
                                .requestFields(
                                        fieldWithPath("id").description("리소스 ID").optional(),
                                        fieldWithPath("name").description("리소스 이름"),
                                        fieldWithPath("urlPattern").description("URL 접근 패턴"),
                                        fieldWithPath("method").description("HTTP Method"),
                                        fieldWithPath("roleIds").description("URL 패턴에 접근이 가능한 권한"),
                                        fieldWithPath("description").description("정책에 대한 설명")
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }

    @Test
    @DisplayName("리소스 수정 및 문서화")
    void updateResources() throws Exception {
        ResourcesSaveRequest request = new ResourcesSaveRequest(
                1L, "관리자 기능 사용", "/admins/**", Method.ALL, List.of(1L),
                "관리자 페이지에 접근 및 기능을 허용 하는 정책");

        mockMvc.perform(post("/api/v1/resources/access/{resourceId}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("resource-update",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Resource API")
                                .summary("리소스 수정")
                                .requestFields(
                                        fieldWithPath("id").description("리소스 ID").optional(),
                                        fieldWithPath("name").description("리소스 이름"),
                                        fieldWithPath("urlPattern").description("URL 접근 패턴"),
                                        fieldWithPath("method").description("HTTP Method"),
                                        fieldWithPath("roleIds").description("URL 패턴에 접근이 가능한 권한"),
                                        fieldWithPath("description").description("정책에 대한 설명")
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));

    }

    @Test
    @DisplayName("리소스 삭제 및 문서화")
    void deleteResources() throws Exception {

        mockMvc.perform(delete("/api/v1/resources/access?ids=1,2,3")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("resource-delete",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Resource API")
                                .summary("리소스 삭제")
                                .queryParameters(
                                        parameterWithName("ids")
                                                .description("삭제할 리소스 ID 목록"))
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }

    @Test
    @DisplayName("권한 추가 및 문서화")
    void saveRole() throws Exception {

        RoleSaveRequest request = new RoleSaveRequest(
                null, "ROLE_ADMIN", "관리자", 1L, true);

        mockMvc.perform(post("/api/v1/resources/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("role-save",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Resource API")
                                .summary("권한 추가")
                                .requestFields(
                                        fieldWithPath("id").description("권한 ID").optional(),
                                        fieldWithPath("roleKey").description("권한 키"),
                                        fieldWithPath("roleName").description("권한 이름"),
                                        fieldWithPath("parentId").description("상위 권한 ID"),
                                        fieldWithPath("isAdmin").description("관리자 권한 여부(true/false)")
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }

    @Test
    @DisplayName("권한 수정 및 문서화")
    void updateRole() throws Exception {

        RoleSaveRequest request = new RoleSaveRequest(
                1L, "ROLE_GUEST", "게스트", null, true);

        mockMvc.perform(post("/api/v1/resources/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("role-update",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Resource API")
                                .summary("권한 수정")
                                .requestFields(
                                        fieldWithPath("id").description("권한 ID").optional(),
                                        fieldWithPath("roleKey").description("권한 키"),
                                        fieldWithPath("roleName").description("권한 이름"),
                                        fieldWithPath("parentId").description("상위 권한 ID"),
                                        fieldWithPath("isAdmin").description("관리자 권한 여부(true/false)")
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }

    @Test
    @DisplayName("권한 삭제 및 문서화")
    void deleteRole() throws Exception {

        mockMvc.perform(delete("/api/v1/resources/role?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("role-delete",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Resource API")
                                .summary("권한 삭제")
                                .queryParameters(
                                        parameterWithName("id")
                                                .description("삭제할 권한 ID"))
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }
}
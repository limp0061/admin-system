package com.project.admin_system.user.presentation;

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
import com.project.admin_system.user.application.dto.AdminRoleRequest;
import com.project.admin_system.user.application.service.AdminUserService;
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

@WebMvcTest(controllers = AdminUserApiController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureRestDocs
class AdminUserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminUserService adminUserService;


    @Test
    @DisplayName("관리자 추가 및 문서화")
    void saveAdmin() throws Exception {
        AdminRoleRequest request = new AdminRoleRequest(1L, 1L, List.of("127.0.0.1", "192.168.1.0"));

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("admin-save",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Admin API")
                                .summary("관리자 추가")
                                .requestFields(
                                        fieldWithPath("id").description("관리자 ID"),
                                        fieldWithPath("roleId").description("권한 ID"),
                                        fieldWithPath("ips").description("허용 IP 목록").optional()
                                )
                                .build()
                        )));
    }

    @Test
    @DisplayName("관리자 수정 및 문서화")
    void updateAdmin() throws Exception {
        AdminRoleRequest request = new AdminRoleRequest(1L, 2L, List.of("127.0.0.1", "192.168.1.1"));

        mockMvc.perform(post("/api/v1/admins/{userId}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("admin-update",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Admin API")
                                .summary("관리자 수정")
                                .requestFields(
                                        fieldWithPath("id").description("관리자 ID"),
                                        fieldWithPath("roleId").description("권한 ID"),
                                        fieldWithPath("ips").description("허용 IP 목록").optional()
                                )
                                .build()
                        )));
    }

    @Test
    @DisplayName("관리자 삭제 및 문서화")
    void deleteNotice() throws Exception {

        mockMvc.perform(delete("/api/v1/admins?ids=1,2,3")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("admin-delete",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Admin API")
                                .summary("권리자 삭제")
                                .queryParameters(
                                        parameterWithName("ids")
                                                .description("삭제할 관리자 ID 목록"))
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }
}
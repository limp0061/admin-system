package com.project.admin_system.dept.presentation;


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
import com.project.admin_system.dept.application.dto.DeptSaveRequest;
import com.project.admin_system.dept.application.service.DeptService;
import com.project.admin_system.dept.application.validate.DeptValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = DeptApiController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureRestDocs
class DeptApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeptService deptService;
    @MockitoBean
    private DeptValidator deptValidator;

    @Test
    @DisplayName("부서 추가 및 문서화")
    void createDeptTest() throws Exception {

        DeptSaveRequest request = new DeptSaveRequest(null, "개발팀", "0104", 0L, 1, true);

        mockMvc.perform(post("/api/v1/depts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("dept-create",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Dept API")
                                .summary("부서 추가")
                                .requestFields(
                                        fieldWithPath("id").description("부서 고유 ID").optional(),
                                        fieldWithPath("deptName").description("부서명"),
                                        fieldWithPath("deptCode").description("부서 코드"),
                                        fieldWithPath("upperDeptId").description("상위부서 ID"),
                                        fieldWithPath("sortOrder").description("정렬"),
                                        fieldWithPath("isActive").description("활성화 여부")
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }

    @Test
    @DisplayName("부서 수정 및 문서화")
    void updateDeptTest() throws Exception {

        DeptSaveRequest request = new DeptSaveRequest(1L, "디자인팀", "0103", 0L, 2, true);

        mockMvc.perform(post("/api/v1/depts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("dept-update",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Dept API")
                                .summary("부서 수정")
                                .requestFields(
                                        fieldWithPath("id").description("부서 고유 ID"),
                                        fieldWithPath("deptName").description("부서명"),
                                        fieldWithPath("deptCode").description("부서 코드"),
                                        fieldWithPath("upperDeptId").description("상위부서 ID"),
                                        fieldWithPath("sortOrder").description("정렬"),
                                        fieldWithPath("isActive").description("활성화 여부")
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }

    @Test
    @DisplayName("부서 삭제 및 문서화")
    void deleteDeptTest() throws Exception {

        mockMvc.perform(delete("/api/v1/depts/{id}", 1
                )).andExpect(status().isOk())
                .andDo(document("dept-delete",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Dept API")
                                .summary("부서 삭제")
                                .pathParameters(
                                        parameterWithName("id").description("삭제하는 부서의 ID"))
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));

    }
}
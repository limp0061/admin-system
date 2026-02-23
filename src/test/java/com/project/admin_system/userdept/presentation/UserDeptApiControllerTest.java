package com.project.admin_system.userdept.presentation;


import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.project.admin_system.common.util.ApiDocumentUtils.SUCCESS_RESPONSE_FIELDS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.admin_system.userdept.application.dto.UserDeptDto;
import com.project.admin_system.userdept.application.dto.UserDeptRequest;
import com.project.admin_system.userdept.application.service.UserDeptService;
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

@WebMvcTest(controllers = UserDeptApiController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureRestDocs
class UserDeptApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserDeptService userDeptService;


    @Test
    @DisplayName("사용자 부서 변경 테스트 및 문서화")
    void updateUserDeptTest() throws Exception {
        UserDeptDto user1 = new UserDeptDto(1L, 10L);
        UserDeptDto user2 = new UserDeptDto(2L, 10L);

        List<UserDeptDto> userDepts = List.of(user1, user2);

        UserDeptRequest request = new UserDeptRequest(2L, userDepts, "EDIT");

        mockMvc.perform(post("/api/v1/user-depts/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("user-dept-change",
                        resource(ResourceSnippetParameters.builder()
                                .tag("User Dept API")
                                .summary("사용자 부서 변경/제거")
                                .description("선택한 사용자들의 부서를 변경하거나 제거, mode가 'EDIT'일 경우 targetDeptId가 필수입니다.")
                                .requestFields(
                                        fieldWithPath("targetDeptId").description("이동할 대상 부서 ID (제거 시만 null 가능)")
                                                .optional(),
                                        fieldWithPath("mode").description("작업 모드 (EDIT: 변경, REMOVE: 제거)"),
                                        fieldWithPath("userDepts").description("변경 대상 사용자 목록"),
                                        fieldWithPath("userDepts[].userId").description("사용자 ID"),
                                        fieldWithPath("userDepts[].deptId").description("현재 소속된 부서 ID")
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }
}
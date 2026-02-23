package com.project.admin_system.notice.presentation;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.project.admin_system.common.util.ApiDocumentUtils.SUCCESS_RESPONSE_FIELDS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.admin_system.notice.application.dto.NoticeSaveRequest;
import com.project.admin_system.notice.application.service.NoticeService;
import com.project.admin_system.notice.domain.NoticeType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = NoticeAPiController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureRestDocs
class NoticeAPiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoticeService noticeService;

    @Test
    @DisplayName("공지 추가, 수정 및 문서화")
    void saveNotice() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        NoticeSaveRequest request = new NoticeSaveRequest(
                1L, NoticeType.NORMAL, "테스트 입니다", true, true, "공지 테스트입니다", now, now.plusDays(7)
        );

        mockMvc.perform(post("/api/v1/notices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("notice-save",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Notice API")
                                .summary("공지 저장")
                                .requestFields(
                                        fieldWithPath("id").description("공지사항 ID (수정 시 필수, 등록 시 null)").optional(),
                                        fieldWithPath("type").description("공지사항 유형 (NORMAL: 일반, SYSTEM: 시스템)"),
                                        fieldWithPath("title").description("공지사항 제목"),
                                        fieldWithPath("content").description("공지사항 내용 (HTML 태그 포함 가능)"),
                                        fieldWithPath("isRealTimeNoticed").description("실시간 알림 여부 (true/false)"),
                                        fieldWithPath("isForce").description("알림 강제 발송 여부 (true/false)"),
                                        fieldWithPath("startAt").description("게시 시작 일시 (yyyy-MM-dd HH:mm)")
                                                .optional()
                                                .attributes(key("format").value("yyyy-MM-dd HH:mm")),
                                        fieldWithPath("endAt").description("게시 종료 일시 (yyyy-MM-dd HH:mm)")
                                                .optional()
                                                .attributes(key("format").value("yyyy-MM-dd HH:mm"))
                                )
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }

    @Test
    @DisplayName("공지 삭제 및 문서화")
    void deleteNotice() throws Exception {
        mockMvc.perform(delete("/api/v1/notices?ids=1,2,3")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("notice-delete",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Notice API")
                                .summary("공지 삭제")
                                .queryParameters(
                                        parameterWithName("ids")
                                                .description("삭제할 공지 ID 목록"))
                                .responseFields(SUCCESS_RESPONSE_FIELDS)
                                .build())));
    }

}
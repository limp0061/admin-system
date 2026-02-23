package com.project.admin_system.common.util;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;
import java.util.Objects;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class ApiDocumentUtils {
    // 성공 응답 필드
    public static final List<FieldDescriptor> SUCCESS_RESPONSE_FIELDS = List.of(
            fieldWithPath("message").description("결과 메시지"),
            fieldWithPath("data").description("데이터 (성공 시 보통 null)").optional()
    );

    // 에러 응답 필드
    public static final List<FieldDescriptor> ERROR_RESPONSE_FIELDS = List.of(
            fieldWithPath("code").description("에러 코드"),
            fieldWithPath("message").description("에러 메시지"),
            fieldWithPath("status").description("HTTP 상태값"),

            // 리스트 안의 객체 필드들도 데이터가 없을 때를 대비해 type 명시
            fieldWithPath("fieldErrors").type(JsonFieldType.ARRAY)
                    .description("상세 에러 내역 (유효성 검사 실패 시 존재)").optional(),
            fieldWithPath("fieldErrors[].field").type(JsonFieldType.STRING)
                    .description("에러가 발생한 필드명").optional(),
            fieldWithPath("fieldErrors[].reason").type(JsonFieldType.STRING)
                    .description("에러 상세 사유").optional()
    );
}

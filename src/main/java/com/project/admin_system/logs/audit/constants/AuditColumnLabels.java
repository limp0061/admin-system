package com.project.admin_system.logs.audit.constants;

import com.project.admin_system.logs.audit.domain.AuditTarget;
import java.util.Map;

public class AuditColumnLabels {

    public static final Map<String, String> USER = Map.of(
            "emailId", "이메일",
            "name", "이름",
            "position", "직급",
            "userCode", "사번",
            "gender", "성별",
            "userStatus", "계정 상태",
            "roleName", "권한"
    );

    public static final Map<String, String> ADMIN = Map.of(
            "roleName", "권한",
            "ips", "IP 리스트"
    );

    public static final Map<String, String> DEPT = Map.of(
            "deptCode", "부서 코드",
            "deptName", "부서명",
            "upperDeptId", "상위 부서 코드",
            "sortOrder", "정렬",
            "isActive", "활성 여부"
    );
    public static final Map<String, String> USER_DEPT = Map.of(
            "name", "이름",
            "deptName", "부서명"
    );
    public static final Map<String, String> ROLE = Map.of();
    public static final Map<String, String> RESOURCE = Map.of(
            "name", "리소스 명",
            "urlPattern", "URL Pattern",
            "method", "Method",
            "roles", "권한",
            "description", "설명"
    );
    public static final Map<String, String> NOTICE = Map.of(
            "type", "공지 타입",
            "title", "공지 제목",
            "startAt", "시작 기간",
            "endAt", "종료 기간",
            "isRealtimeNotified", "실시간 발송",
            "isForce", "강제 발송"
    );

    public static Map<String, String> getMapByTargetEntity(AuditTarget targetEntity) {
        return switch (targetEntity) {
            case USER -> USER;
            case ADMIN -> ADMIN;
            case DEPT -> DEPT;
            case USER_DEPT -> USER_DEPT;
            case ROLE -> ROLE;
            case RESOURCE -> RESOURCE;
            case NOTICE -> NOTICE;
        };
    }
}
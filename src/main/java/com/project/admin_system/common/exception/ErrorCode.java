package com.project.admin_system.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 입력 값입니다."),

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_IDS_CONTAIN(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자가 포함되어 있습니다."),
    DUPLICATE_EMAIL_ID(HttpStatus.CONFLICT, "이미 사용 중인 이메일 주소입니다."),
    DUPLICATE_USER_CODE(HttpStatus.CONFLICT, "이미 사용 중인 사번입니다."),
    USER_NOT_AUTHENTICATED(HttpStatus.FORBIDDEN, "로그인 권한이 없습니다."),

    USER_EXPIRED(HttpStatus.UNAUTHORIZED, "사용자 계정이 만료되었습니다."),
    PASSWORD_EXPIRED(HttpStatus.UNAUTHORIZED, "비밀번호가 만료되었습니다."),
    USER_STATUS_DISABLED(HttpStatus.UNAUTHORIZED, "계정이 비활성화 되어있습니다."),
    USER_STATUS_LOCKED(HttpStatus.UNAUTHORIZED, "계정이 잠겨있습니다."),
    IP_DENIED(HttpStatus.FORBIDDEN, "허용되지 않은 IP 입니다."),

    DUPLICATE_ROLE_ASSIGNMENT(HttpStatus.CONFLICT, "이미 권한이 있는 사용자입니다."),

    // 부서
    DUPLICATE_DEPT_CODE(HttpStatus.CONFLICT, "이미 사용 중인 부서코드 입니다."),
    DEPT_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 부서입니다."),
    UPPER_DEPT_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상위 부서입니다."),
    USER_DEPT_NOT_FOUND(HttpStatus.BAD_REQUEST, "부서 구성원의 정보가 일치하지 았습니다."),
    DEPT_CAN_NOT_DELETE(HttpStatus.BAD_REQUEST, "해당 부서를 삭제할 수 없습니다"),

    // 공지
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공지입니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다."),

    // 파일
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다."),
    FILE_DIR_NOT_EXIST(HttpStatus.NOT_FOUND, "파일 경로가 존재하지 않습니다."),

    // 리소스
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    DUPLICATE_URL_PATTERN(HttpStatus.CONFLICT, "중복된 URL Pattern 입니다."),

    // 권한
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 권한입니다."),
    ROLE_HAS_USERS(HttpStatus.CONFLICT, "해당 권한을 사용 중인 사용자가 있습니다."),
    ROLE_HAS_URL_PATTERN(HttpStatus.CONFLICT, "해당 권한에 연결된 리소스가 있습니다."),
    ROLE_HAS_CHILDREN(HttpStatus.CONFLICT, "하위 권한이 존재하여 삭제할 수 없습니다."),
    DUPLICATE_ROLE_KEY(HttpStatus.CONFLICT, "중복된 권한 키가 있습니다."),


    // 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // 파일 용량 초과
    EXCEED_MAX_UPLOAD_SIZE(HttpStatus.PAYLOAD_TOO_LARGE, "파일 용량이 너무 큽니다. (최대 10MB)"),

    // IP 정규식
    INVALID_IP_FORMAT(HttpStatus.BAD_REQUEST, "IP의 형식이 올바르지 않습니다."),
    ;

    private final HttpStatus status;
    private final String message;

}

SET NAMES utf8mb4;
-- 1. 리소스 데이터 삽입 (ID 컬럼 완전 제외)
-- Admin API
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/api/v1/admins', 'DELETE', '관리자 삭제', '관리자 권한 삭제 API'),
       ('/api/v1/admins', 'POST', '관리자 추가', '새로운 관리자 등록 API'),
       ('/api/v1/admins/*/update', 'POST', '관리자 수정', '관리자 정보 업데이트 API');

-- Dept API
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/api/v1/depts/*', 'DELETE', '부서 삭제', '부서 정보 삭제 API'),
       ('/api/v1/depts', 'POST', '부서 저장', '신규 부서 등록/수정 API');

-- Notice API
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/api/v1/notices', 'DELETE', '공지 삭제', '공지사항 삭제 API'),
       ('/api/v1/notices', 'POST', '공지 저장', '공지사항 작성 및 저장 API');

-- Resource API
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/api/v1/resources/access', 'DELETE', '리소스 삭제', '접근 제어 리소스 삭제 API'),
       ('/api/v1/resources/role', 'DELETE', '권한 삭제', '특정 역할 권한 삭제 API'),
       ('/api/v1/resources/access', 'POST', '리소스 추가', '신규 접근 리소스 등록 API'),
       ('/api/v1/resources/role', 'POST', '권한 추가', '역할별 권한 매핑 추가 API'),
       ('/api/v1/resources/access/*/update', 'POST', '리소스 수정', '리소스 정보 업데이트 API');

-- User API
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/api/v1/users', 'POST', '사용자 추가', '신규 사용자 계정 생성 API'),
       ('/api/v1/users/changeStatus', 'POST', '계정 상태 변경', '사용자 활성/비활성 상태 변경 API'),
       ('/api/v1/users/search', 'GET', '사용자 검색', '사용자 검색 API'),
       ('/api/v1/users/*/update', 'POST', '사용자 수정', '사용자 상세 정보 업데이트 API');

-- User Dept API
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/api/v1/user-depts/change', 'POST', '사용자 부서 변경', '사용자의 소속 부서 변경 및 제거 API');

-- File API
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/api/v1/files/editor-upload', 'POST', '에디터 파일 업로드', '에디터에 파일 업로드 요청 API'),
       ('/api/v1/files/viewer/*', 'GET', '업로드 된 파일 미리보기', 'S3에 업로드 된 이미지 파일을 미리보는 API');

-- Notification API
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/api/v1/notifications/subscribe', 'GET', '실시간 알림 구독', 'SSE 연결을 통한 실시간 알림 수신 API'),
       ('/api/v1/notifications/unread-count', 'GET', '미확인 알림 개수 조회', '읽지 않은 알림 카운트 조회 API'),
       ('/api/v1/notifications', 'GET', '알림 목록 로드', '타입별 알림 리스트 조회 API'),
       ('/api/v1/notifications/readAll', 'POST', '알림 전체 읽음 처리', '모든 알림 읽음 상태로 변경 API'),
       ('/api/v1/notifications/send', 'POST', '알림 발송', '특정 사용자에게 알림 발송 API');

-- 일반 페이지 (매니저 이상)
INSERT INTO resource (url_pattern, method, resource_name, description)
values ('/dashboard', 'GET', '대시보드 메인', '대시보드 메인 화면'),
       ('/logs/user', 'GET', '사용자 로그', '사용자 로그 화면'),
       ('/logs/admin', 'GET', '관리자 로그', '관리자 로그 화면'),
       ('/notices', 'GET', '공지사항 메인', '공지사항 목록 화면'),
       ('/notices/*', 'GET', '공지사항 상세 조회', '공지사항 내용 확인'),
       ('/depts', 'GET', '부서 관리 메인', '부서 목록 및 사용자 조회 화면'),
       ('/depts/tree', 'GET', '부서 트리 뷰', '전체 부서 조직도 확인 화면'),
       ('/resources/access', 'GET', '리소스 메인', '리소스 목록 화면'),
       ('/resources/role', 'GET', '권한 메인', '권한 목록 화면 및 트리뷰'),
       ('/statistics/login', 'GET', '로그인 통계', '로그인 통계 화면'),
       ('/statistics/system', 'GET', '시스템 통계', '시스템 통게 화면'),
       ('/admins', 'GET', '관리자 메인', '관리자 목록 화면'),
       ('/users', 'GET', '사용자 목록 메인', '전체 사용자 리스트 조회 화면'),
       ('/users/approvals', 'GET', '가입 승인 대기 목록', '신규 가입자 승인 관리 화면'),
       ('/users/deleted', 'GET', '삭제 사용자 목록', '탈퇴 및 삭제된 사용자 추적 화면');

-- 관리용 모달 (어드민 이상)
INSERT INTO resource (url_pattern, method, resource_name, description)
VALUES ('/notices/modal/add', 'GET', '공지사항 추가 팝업', '신규 공지사항 등록 모달'),
       ('/notices/modal/edit', 'GET', '공지사항 수정 팝업', '기존 공지사항 변경 모달'),
       ('/notices/modal/delete', 'GET', '공지사항 삭제 팝업', '공지사항 삭제 전 유효성 검사 및 확인 모달'),
       ('/notices/modal/view', 'GET', '공지사항 전체 팝업', '최근 30개의 공지사항 목록'),
       ('/depts/modal/add', 'GET', '부서 추가 팝업', '신규 부서 등록 모달'),
       ('/depts/modal/edit', 'GET', '부서 수정 팝업', '기존 부서 정보 변경 모달'),
       ('/depts/modal/delete', 'GET', '부서 삭제 팝업', '부서 삭제 전 유효성 검사 및 확인 모달'),
       ('/resources/access/modal/add', 'GET', '리소스 추가 모달', '신규 URL 리소스 등록 모달'),
       ('/resources/access/modal/edit', 'GET', '리소스 수정 모달', '리소스 정보 및 매핑 역할 수정 모달'),
       ('/resources/access/modal/delete', 'GET', '리소스 삭제 모달', '리소스 멀티 삭제 확인 모달'),
       ('/resources/role/modal/add', 'GET', '역할 추가 모달', '신규 역할 등록 모달'),
       ('/resources/role/modal/edit', 'GET', '역할 수정 모달', '역할 정보 및 계층 수정 모달'),
       ('/resources/role/modal/delete', 'GET', '역할 삭제 모달', '역할 삭제 전 유효성 검사 모달'),
       ('/admins/modal/add', 'GET', '관리자 추가 모달', '신규 관리자 추가 모달'),
       ('/admins/modal/edit', 'GET', '관리자 수정 모달', '기존 관리자 수정 모달'),
       ('/admins/modal/delete', 'GET', '관리자 삭제 모달', '관리자 삭제 전 유효성 검사 모달'),
       ('/users/modal/add', 'GET', '사용자 추가 모달', '신규 사용자 수동 등록 팝업'),
       ('/users/modal/edit', 'GET', '사용자 수정 모달', '사용자 정보 및 권한 수정 팝업'),
       ('/users/modal/changeStatus', 'GET', '상태 변경 모달', '일괄 승인/잠금/삭제 처리 팝업'),
       ('/user-depts/modal/form', 'GET', '부서원 추가 폼 모달', '부서원 추가를 위한 사용자 검색 및 선택 팝업'),
       ('/user-depts/modal/form/table', 'GET', '부서원 검색 테이블', '모달 내 사용자 검색 결과 비동기 업데이트 전용'),
       ('/user-depts/modal/delete', 'GET', '부서원 삭제 확인 모달', '부서에서 사용자 제외 전 유효성 확인 팝업');

-- 모든 관리자(Role ID: 1, 2, 3)가 접근 가능한 페이지 리소스
-- 모든 관리자 페이지(GET) 조회 권한
INSERT INTO resource_roles (resource_id, role_id)
SELECT resource_id, 3
FROM resource
WHERE method = 'GET'
  AND url_pattern NOT LIKE '%/modal/%'
  AND url_pattern NOT LIKE '/resources/%';

-- 모든 관리용 모달(GET) 및 조작 API (POST, DELETE)
INSERT INTO resource_roles (resource_id, role_id)
SELECT resource_id, 2
FROM resource
WHERE (url_pattern LIKE '%/modal/%'
    OR url_pattern LIKE '/api/v1/%')
  AND url_pattern NOT LIKE '/api/v1/resources/%'
  AND url_pattern NOT LIKE '/resources/%';

-- 시스템 관리(Resource, Role) 관련 모든 리소스
INSERT INTO resource_roles (resource_id, role_id)
SELECT resource_id, 1
FROM resource
WHERE url_pattern LIKE '/api/v1/resources/%'
   OR url_pattern LIKE '/resources/%';

-- 모든 유저(4)가 접근 가능한 알림 공통 API
INSERT INTO resource_roles (resource_id, role_id)
SELECT resource_id, 6
FROM resource
WHERE url_pattern IN ('/api/v1/notifications/subscribe', '/api/v1/notifications/unread-count', '/api/v1/notifications',
                      '/api/v1/files/viewer/*');
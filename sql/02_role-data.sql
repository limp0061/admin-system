SET NAMES utf8mb4;
-- 1. 슈퍼 관리자 (Root)
INSERT INTO roles (role_id, role_key, role_name, depth, is_admin, parent_id)
VALUES (1, 'ROLE_SUPER', '슈퍼 관리자', 0, true, NULL);

-- 2. 일반 관리자 (부모: 1)
INSERT INTO roles (role_id, role_key, role_name, depth, is_admin, parent_id)
VALUES (2, 'ROLE_ADMIN', '일반 관리자', 1, true, 1);

-- 3. 매니저 (부모: 2)
INSERT INTO roles (role_id, role_key, role_name, depth, is_admin, parent_id)
VALUES (3, 'ROLE_MANAGER', '매니저', 2, true, 2);

-- 4. 일반 사용자 (부모: 3)
INSERT INTO roles (role_id, role_key, role_name, depth, is_admin, parent_id)
VALUES (4, 'ROLE_USER', '일반 사용자', 3, false, 3);

-- 5. 게스트 (부모: 3)
INSERT INTO roles (role_id, role_key, role_name, depth, is_admin, parent_id)
VALUES (5, 'ROLE_GUEST', '게스트', 3, false, 3);

-- 6. 전체 허용 (Root - 별도 계층)
INSERT INTO roles (role_id, role_key, role_name, depth, is_admin, parent_id)
VALUES (6, 'ROLE_ANONYMOUS', '전체 허용', 0, false, NULL);
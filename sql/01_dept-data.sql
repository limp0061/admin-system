SET NAMES utf8mb4;
-- 1 depth (upper_dept_id = 1)
INSERT INTO dept (dept_code, dept_name, upper_dept_id, is_active, sort_order, depth, created_at, updated_at)
VALUES ('DEV', '개발본부', null, TRUE, 1, 1, NOW(), NOW());

INSERT INTO dept (dept_code, dept_name, upper_dept_id, is_active, sort_order, depth, created_at, updated_at)
VALUES ('MGMT', '경영지원본부', null, TRUE, 2, 1, NOW(), NOW());

-- 2 Depth (상위 본부의 ID인 2 또는 3을 매핑)
INSERT INTO dept (dept_code, dept_name, upper_dept_id, is_active, sort_order, depth, created_at, updated_at)
VALUES ('DEV_FE', '프론트엔드팀', 1, TRUE, 1, 2, NOW(), NOW());

INSERT INTO dept (dept_code, dept_name, upper_dept_id, is_active, sort_order, depth, created_at, updated_at)
VALUES ('DEV_BE', '백엔드팀', 1, TRUE, 2, 2, NOW(), NOW());

INSERT INTO dept (dept_code, dept_name, upper_dept_id, is_active, sort_order, depth, created_at, updated_at)
VALUES ('MGMT_HR', '인사팀', 2, TRUE, 1, 2, NOW(), NOW());

INSERT INTO dept (dept_code, dept_name, upper_dept_id, is_active, sort_order, depth, created_at, updated_at)
VALUES ('MGMT_GA', '총무팀', 2, FALSE, 2, 2, NOW(), NOW());
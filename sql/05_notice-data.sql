SET NAMES utf8mb4;
-- 1. 60개의 공지사항 생성 (0~59)
INSERT INTO notice (notice_type, title, content, is_realtime_notified, is_force, start_at, end_at, created_at, updated_at, created_by)
SELECT
    CASE WHEN MOD(n, 5) = 0 THEN 'SYSTEM' ELSE 'NORMAL' END AS type,
    CASE
        WHEN MOD(n, 5) = 0 THEN
            CASE (n % 3)
                WHEN 0 THEN CONCAT('정기 시스템 점검 안내 (ID: ', n, ')')
                WHEN 1 THEN '서비스 안정화를 위한 보안 패치 작업 공지'
                ELSE '데이터베이스 최적화 작업에 따른 서비스 일시 중단 안내'
                END
        ELSE
            CASE (n % 4)
                WHEN 0 THEN CONCAT('신규 기능 업데이트 안내 (v1.', n, ')')
                WHEN 1 THEN '서비스 이용약관 개정 안내'
                WHEN 2 THEN '개인정보 처리방침 변경 고지'
                ELSE CONCAT('운영팀에서 알려드립니다 (', n, '번 공지)')
                END
        END AS title,
    CONCAT('안녕하세요. 운영팀입니다. 해당 내용은 테스트를 위한 자동 생성 콘텐츠입니다. ID: ', n) AS content,
    CASE WHEN MOD(n, 10) = 0 THEN TRUE ELSE FALSE END AS is_realtime,
    FALSE,
    -- [수정] TIMESTAMPADD 사용
    TIMESTAMPADD(DAY, n - 30, CURRENT_TIMESTAMP),
    CASE
        WHEN MOD(n, 12) = 0 THEN NULL
        WHEN n < 20 THEN TIMESTAMPADD(DAY, -10, CURRENT_TIMESTAMP)
        WHEN n < 40 THEN TIMESTAMPADD(DAY, 30, CURRENT_TIMESTAMP)
        ELSE TIMESTAMPADD(DAY, 60, CURRENT_TIMESTAMP)
        END,
    TIMESTAMPADD(MINUTE, -n, CURRENT_TIMESTAMP),
    TIMESTAMPADD(MINUTE, 1, TIMESTAMPADD(MINUTE, -n, CURRENT_TIMESTAMP)),
    'admin_test'
FROM (
         SELECT (a.N + b.N * 10) AS n
         FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
              (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) b
     ) AS numbers;

-- 2. NotificationMaster (알림 마스터)
INSERT INTO notification_master (notification_id, notice_id, title, url, is_force, created_at, updated_at, created_by)
VALUES
    (1, 10, '정기 시스템 점검 안내가 등록되었습니다.', '/notices/10', FALSE, TIMESTAMPADD(HOUR, -2, CURRENT_TIMESTAMP), TIMESTAMPADD(HOUR, -2, CURRENT_TIMESTAMP), 'system'),
    (2, 25, '신규 기능 업데이트 안내 (v1.25)가 등록되었습니다.', '/notices/25', FALSE, TIMESTAMPADD(MINUTE, -45, CURRENT_TIMESTAMP), TIMESTAMPADD(MINUTE, -45, CURRENT_TIMESTAMP), 'admin'),
    (3, 30, '보안 패치 작업 공지사항을 확인해주세요.', '/notices/30', TRUE, TIMESTAMPADD(MINUTE, -10, CURRENT_TIMESTAMP), TIMESTAMPADD(MINUTE, -10, CURRENT_TIMESTAMP), 'system');

-- 3. NotificationRead (읽음 처리)
INSERT INTO notification_read (notification_read_id, notification_id, user_id, created_at, updated_at, created_by)
VALUES
    (1, 1, 1, TIMESTAMPADD(HOUR, -1, CURRENT_TIMESTAMP), TIMESTAMPADD(HOUR, -1, CURRENT_TIMESTAMP), 'user_1'),
    (2, 2, 1, TIMESTAMPADD(MINUTE, -5, CURRENT_TIMESTAMP), TIMESTAMPADD(MINUTE, -5, CURRENT_TIMESTAMP), 'user_1');
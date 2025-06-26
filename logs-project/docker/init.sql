-- postgres-init/init.sql

-- Đảm bảo bảng không tồn tại trước khi tạo để tránh lỗi khi khởi động lại
DROP TABLE IF EXISTS storage_event;

-- 1. Tạo bảng storage_event
CREATE TABLE storage_event (
    id              VARCHAR(64) PRIMARY KEY,
    target_type     VARCHAR(64) NOT NULL,
    target_id       VARCHAR(64) NOT NULL,
    subject_type    VARCHAR(64) NOT NULL,
    subject_id      VARCHAR(64) NOT NULL,
    action          VARCHAR(64) NOT NULL,
    data            JSONB,
    correlation_id  VARCHAR(64),
    created_at      BIGINT  NOT NULL
);

-- Thông báo ra console của container để dễ debug
\echo 'Table storage_event created.'

-- 2. Chèn dữ liệu mẫu
\echo 'Inserting sample data...'

-- Kịch bản 1: Luồng thành công của người dùng Alice (user_alice_101)
INSERT INTO storage_event (id, target_type, target_id, subject_type, subject_id, action, data, correlation_id, created_at) VALUES
('evt_20230115_001', 'User', 'user_alice_101', 'User', 'user_alice_101', 'UserLoggedIn', 
    '{"ip_address": "203.0.113.25", "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36"}', 
    'corr_alice_success_abc', 1673740800000),

('evt_20230115_002', 'File', 'file_project_plan_v1', 'User', 'user_alice_101', 'FileUploaded',
    '{"filename": "Project_Plan_Q1.docx", "size_bytes": 1572864, "mime_type": "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}',
    'corr_alice_success_abc', 1673740860000),

('evt_20230115_003', 'Permission', 'perm_read_proj_plan', 'User', 'user_alice_101', 'PermissionGranted',
    '{"grantee_id": "user_bob_202", "resource_type": "File", "resource_id": "file_project_plan_v1", "permission": "read"}',
    'corr_alice_success_abc', 1673740920000),

('evt_20230115_004', 'User', 'user_alice_101', 'User', 'user_alice_101', 'UserLoggedOut',
    '{"session_duration_sec": 180}',
    'corr_alice_success_abc', 1673740980000);


-- Kịch bản 2: Luồng đăng nhập thất bại (Brute-force attack)
INSERT INTO storage_event (id, target_type, target_id, subject_type, subject_id, action, data, correlation_id, created_at) VALUES
('evt_20230115_005', 'User', 'user_admin', 'Anonymous', 'anonymous', 'UserLoginFailed',
    '{"reason": "InvalidCredentials", "ip_address": "198.51.100.10", "user_agent": "Python/3.9 aiohttp/3.8.1"}',
    'corr_bruteforce_xyz', 1673741100000),

('evt_20230115_006', 'User', 'user_admin', 'Anonymous', 'anonymous', 'UserLoginFailed',
    '{"reason": "InvalidCredentials", "ip_address": "198.51.100.10", "user_agent": "Python/3.9 aiohttp/3.8.1"}',
    'corr_bruteforce_xyz', 1673741102000),

('evt_20230115_007', 'User', 'user_admin', 'Anonymous', 'anonymous', 'UserLoginFailed',
    '{"reason": "InvalidCredentials", "ip_address": "198.51.100.10", "user_agent": "Python/3.9 aiohttp/3.8.1"}',
    'corr_bruteforce_xyz', 1673741105000),

('evt_20230115_008', 'Account', 'user_admin', 'System', 'security_module', 'AccountLocked',
    '{"reason": "TooManyFailedLogins", "lockout_duration_min": 30, "source_ip": "198.51.100.10"}',
    'corr_bruteforce_xyz', 1673741106000);


-- Kịch bản 3: Hoạt động của người dùng Bob (user_bob_202)
INSERT INTO storage_event (id, target_type, target_id, subject_type, subject_id, action, data, correlation_id, created_at) VALUES
('evt_20230115_009', 'User', 'user_bob_202', 'User', 'user_bob_202', 'UserLoggedIn',
    '{"ip_address": "203.0.113.50", "user_agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.1 Safari/605.1.15"}',
    'corr_bob_activity_def', 1673741400000),

('evt_20230115_010', 'File', 'file_project_plan_v1', 'User', 'user_bob_202', 'FileAccessed',
    '{"access_type": "download"}',
    'corr_bob_activity_def', 1673741460000),

('evt_20230115_011', 'File', 'file_project_plan_v1', 'User', 'user_bob_202', 'FileDeleteFailed',
    '{"reason": "AccessDenied", "required_permission": "delete"}',
    'corr_bob_activity_def', 1673741520000);


-- Kịch bản 4: Luồng hệ thống (Backup Job)
INSERT INTO storage_event (id, target_type, target_id, subject_type, subject_id, action, data, correlation_id, created_at) VALUES
('evt_20230116_001', 'SystemJob', 'backup_job_daily_01', 'System', 'cron_daemon', 'JobStarted',
    '{"job_name": "DailyFullBackup", "trigger": "scheduled"}',
    'corr_backup_16012023', 1673827200000),

('evt_20230116_002', 'SystemJob', 'backup_job_daily_01', 'System', 'cron_daemon', 'JobCompleted',
    '{"job_name": "DailyFullBackup", "status": "success", "duration_ms": 185000, "files_processed": 10245, "total_size_gb": 25}',
    'corr_backup_16012023', 1673827385000);


-- Kịch bản 5: Luồng tương tác phức tạp của Carol (user_carol_303)
INSERT INTO storage_event (id, target_type, target_id, subject_type, subject_id, action, data, correlation_id, created_at) VALUES
('evt_20230116_003', 'User', 'user_carol_303', 'User', 'user_carol_303', 'UserLoggedIn',
    '{"ip_address": "192.0.2.100", "user_agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36"}',
    'corr_carol_session_ghi', 1673830800000),

('evt_20230116_004', 'Folder', 'folder_marketing_q1_2023', 'User', 'user_carol_303', 'FolderCreated',
    '{"path": "/home/carol/marketing/"}',
    'corr_carol_session_ghi', 1673830820000),

('evt_20230116_005', 'File', 'file_ads_banner_v1', 'User', 'user_carol_303', 'FileUploaded',
    '{"filename": "ads_banner_v1.png", "size_bytes": 819200, "mime_type": "image/png", "parent_folder_id": "folder_marketing_q1_2023"}',
    'corr_carol_session_ghi', 1673830830000),

('evt_20230116_006', 'File', 'file_social_media_plan', 'User', 'user_carol_303', 'FileUploaded',
    '{"filename": "social_media_plan.pdf", "size_bytes": 204800, "mime_type": "application/pdf", "parent_folder_id": "folder_marketing_q1_2023"}',
    'corr_carol_session_ghi', 1673830845000),

('evt_20230116_007', 'File', 'file_ads_banner_v1', 'User', 'user_carol_303', 'FileDeleted',
    '{"filename": "ads_banner_v1.png", "path": "/home/carol/marketing/"}',
    'corr_carol_session_ghi', 1673830900000);


-- Kịch bản 6: Sự kiện lỗi ứng dụng
INSERT INTO storage_event (id, target_type, target_id, subject_type, subject_id, action, data, correlation_id, created_at) VALUES
('evt_20230116_008', 'File', 'file_corrupted_data', 'System', 'file_processor_service', 'ApplicationError',
    '{"error_code": "E_FILE_CORRUPT", "error_message": "Failed to parse file header. File may be corrupted.", "stack_trace": "at com.mycompany.FileProcessor.parseHeader(FileProcessor.java:123)\\n at com.mycompany.FileProcessor.process(FileProcessor.java:78)"}', 
    'corr_system_proc_jkl', 1673831400000);

\echo 'Sample data inserted successfully.'
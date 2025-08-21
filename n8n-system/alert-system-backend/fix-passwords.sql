-- 更新用戶密碼哈希值 (使用測試程序生成的正確哈希)
-- 這些哈希對應 admin123 和 test123 密碼

-- 清理現有數據
DELETE FROM user_roles;
DELETE FROM users;

-- 重新插入用戶 (使用新的正確哈希值)
INSERT INTO users (username, email, password_hash, full_name, status, created_at, updated_at) VALUES
('admin', 'admin@example.com', '$2a$10$HYw7LzVtMzUWMCRI6E1QYuj6o27e25jStNEBfpXsTNmlz8wbZWtGG', '系統管理員', 'ACTIVE', NOW(), NOW()),
('testuser', 'testuser@example.com', '$2a$10$1xBv/SL2wcUR0lG43zcWpuFEFm2sjxh8M/JBXdQvHWi2DevaLsvwC', '測試用戶', 'ACTIVE', NOW(), NOW());

-- 重新設定用戶角色關聯
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ADMIN')),
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'USER')),
((SELECT id FROM users WHERE username = 'testuser'), (SELECT id FROM roles WHERE name = 'USER'));

-- 驗證更新結果
SELECT username, LENGTH(password_hash) as hash_length, SUBSTRING(password_hash, 1, 7) as hash_prefix FROM users;

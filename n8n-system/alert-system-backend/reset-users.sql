-- 重置用戶密碼 - 使用正確的 BCrypt 格式
-- 清除現有數據並重新插入

DELETE FROM user_roles;
DELETE FROM users;

-- 插入 admin 用戶 (密碼: admin123)
INSERT INTO users (username, password_hash, email, full_name, status, created_at, updated_at) 
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@example.com', 'Administrator', 'ACTIVE', NOW(), NOW());

-- 插入 testuser 用戶 (密碼: test123)  
INSERT INTO users (username, password_hash, email, full_name, status, created_at, updated_at)
VALUES ('testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMye7xDJw58pME9JKwwB8q7VYfX9Zt.B1ee', 'test@example.com', 'Test User', 'ACTIVE', NOW(), NOW());

-- 添加角色關聯
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)  
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'testuser' AND r.name = 'USER';

-- 驗證結果
SELECT username, LENGTH(password_hash) as hash_length, SUBSTRING(password_hash, 1, 10) as hash_prefix FROM users;

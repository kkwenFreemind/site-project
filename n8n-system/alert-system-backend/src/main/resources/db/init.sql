-- 智能告警分發系統 - 階段一數據庫初始化腳本
-- 創建時間: 2025-08-21
-- 版本: 1.0

-- ==============================================
-- 用戶認證相關表結構
-- ==============================================

-- 用戶表
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'PENDING')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 創建索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 創建索引
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);

-- 用戶角色關聯表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

-- 登入記錄表
CREATE TABLE IF NOT EXISTS login_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address INET,
    user_agent TEXT,
    status VARCHAR(20) DEFAULT 'SUCCESS' CHECK (status IN ('SUCCESS', 'FAILED', 'BLOCKED'))
);

-- 創建索引
CREATE INDEX IF NOT EXISTS idx_login_logs_user_id ON login_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_login_logs_login_time ON login_logs(login_time);
CREATE INDEX IF NOT EXISTS idx_login_logs_ip_address ON login_logs(ip_address);

-- ==============================================
-- 初始化基礎數據
-- ==============================================

-- 插入默認角色
INSERT INTO roles (name, description) VALUES 
('ADMIN', '系統管理員') 
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES 
('USER', '普通用戶') 
ON CONFLICT (name) DO NOTHING;

-- 創建默認管理員帳號 (密碼: admin123)
-- BCrypt 加密後的密碼
INSERT INTO users (username, email, password_hash, full_name, status) VALUES 
('admin', 'admin@alertsystem.com', '$2a$10$HKVNMqVFGEfQ8YHWqBEzPeqYdE5qX9rJYYyGjEfPPmYzE9QSKVfpC', '系統管理員', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- 分配管理員角色
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 創建測試用戶 (密碼: user123)
INSERT INTO users (username, email, password_hash, full_name, status) VALUES 
('testuser', 'user@alertsystem.com', '$2a$10$HKVNMqVFGEfQ8YHWqBEzPeqYdE5qX9rJYYyGjEfPPmYzE9QSKVfpC', '測試用戶', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- 分配用戶角色
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'testuser' AND r.name = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- ==============================================
-- 添加更新時間觸發器
-- ==============================================

-- 創建更新時間函數
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 為用戶表添加更新時間觸發器
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ==============================================
-- 查詢驗證
-- ==============================================

-- 驗證表結構
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'public' 
AND table_name IN ('users', 'roles', 'user_roles', 'login_logs')
ORDER BY table_name, ordinal_position;

-- 驗證初始數據
SELECT 
    u.username,
    u.email,
    u.status,
    array_agg(r.name) as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id, u.username, u.email, u.status
ORDER BY u.created_at;

-- API資源表
CREATE TABLE IF NOT EXISTS api_resources (
    id BIGSERIAL PRIMARY KEY,
    api_name VARCHAR(100) NOT NULL COMMENT 'API名稱',
    api_path VARCHAR(500) NOT NULL COMMENT 'API路徑',
    http_method VARCHAR(10) NOT NULL COMMENT 'HTTP方法',
    api_description VARCHAR(500) COMMENT 'API描述',
    module_name VARCHAR(50) COMMENT '模組名稱',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否啟用',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公開',
    sort_order INTEGER DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    CONSTRAINT uk_api_path_method UNIQUE (api_path, http_method)
);

-- 角色API權限關聯表
CREATE TABLE IF NOT EXISTS role_api_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    api_resource_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    CONSTRAINT uk_role_api UNIQUE (role_id, api_resource_id),
    CONSTRAINT fk_role_api_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_api_resource FOREIGN KEY (api_resource_id) REFERENCES api_resources(id) ON DELETE CASCADE
);

-- 創建索引
CREATE INDEX IF NOT EXISTS idx_api_path_method ON api_resources (api_path, http_method);
CREATE INDEX IF NOT EXISTS idx_api_enabled ON api_resources (is_enabled);
CREATE INDEX IF NOT EXISTS idx_api_module ON api_resources (module_name);
CREATE INDEX IF NOT EXISTS idx_role_api_role ON role_api_permissions (role_id);
CREATE INDEX IF NOT EXISTS idx_role_api_resource ON role_api_permissions (api_resource_id);

-- 插入基礎API資源數據
INSERT INTO api_resources (api_name, api_path, http_method, api_description, module_name, is_public) VALUES 
-- 認證相關 (公開)
('用戶登入', '/api/auth/login', 'POST', '用戶登入接口', 'auth', TRUE),
('用戶註冊', '/api/auth/register', 'POST', '用戶註冊接口', 'auth', TRUE),
('獲取用戶資料', '/api/auth/profile', 'GET', '獲取當前用戶資料', 'auth', FALSE),
('用戶登出', '/api/auth/logout', 'POST', '用戶登出接口', 'auth', FALSE),

-- 系統管理 (需要管理員權限)
('用戶列表', '/api/system/users', 'GET', '獲取用戶列表', 'system', FALSE),
('創建用戶', '/api/system/users', 'POST', '創建新用戶', 'system', FALSE),
('更新用戶', '/api/system/users/*', 'PUT', '更新用戶信息', 'system', FALSE),
('刪除用戶', '/api/system/users/*', 'DELETE', '刪除用戶', 'system', FALSE),
('切換用戶狀態', '/api/system/users/*/toggle-status', 'PUT', '切換用戶狀態', 'system', FALSE),
('分配角色', '/api/system/users/*/assign-roles', 'PUT', '為用戶分配角色', 'system', FALSE),

-- 角色管理
('角色列表', '/api/system/roles', 'GET', '獲取角色列表', 'system', FALSE),
('創建角色', '/api/system/roles', 'POST', '創建新角色', 'system', FALSE),
('更新角色', '/api/system/roles/*', 'PUT', '更新角色信息', 'system', FALSE),
('刪除角色', '/api/system/roles/*', 'DELETE', '刪除角色', 'system', FALSE),
('分配菜單', '/api/system/roles/*/assign-menus', 'PUT', '為角色分配菜單', 'system', FALSE),

-- 菜單管理
('菜單列表', '/api/system/menus', 'GET', '獲取菜單列表', 'system', FALSE),
('創建菜單', '/api/system/menus', 'POST', '創建新菜單', 'system', FALSE),
('更新菜單', '/api/system/menus/*', 'PUT', '更新菜單信息', 'system', FALSE),
('刪除菜單', '/api/system/menus/*', 'DELETE', '刪除菜單', 'system', FALSE),

-- API權限管理
('API資源列表', '/api/system/api-resources', 'GET', '獲取API資源列表', 'system', FALSE),
('創建API資源', '/api/system/api-resources', 'POST', '創建API資源', 'system', FALSE),
('更新API資源', '/api/system/api-resources/*', 'PUT', '更新API資源', 'system', FALSE),
('刪除API資源', '/api/system/api-resources/*', 'DELETE', '刪除API資源', 'system', FALSE),
('角色API權限', '/api/system/roles/*/api-permissions', 'GET', '獲取角色API權限', 'system', FALSE),
('分配API權限', '/api/system/roles/*/api-permissions', 'PUT', '為角色分配API權限', 'system', FALSE),

-- 日誌管理
('登入日誌', '/api/logs/login-logs', 'GET', '獲取登入日誌', 'logs', FALSE);

-- 為ADMIN角色分配所有API權限
INSERT INTO role_api_permissions (role_id, api_resource_id, created_by)
SELECT 
    r.id as role_id,
    ar.id as api_resource_id,
    1 as created_by
FROM roles r
CROSS JOIN api_resources ar
WHERE r.name = 'ADMIN' AND ar.is_public = FALSE;

-- 為USER角色分配基本權限
INSERT INTO role_api_permissions (role_id, api_resource_id, created_by)
SELECT 
    r.id as role_id,
    ar.id as api_resource_id,
    1 as created_by
FROM roles r
CROSS JOIN api_resources ar
WHERE r.name = 'USER' 
AND ar.api_path IN (
    '/api/auth/profile',
    '/api/auth/logout'
);

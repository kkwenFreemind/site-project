-- ==============================================
-- 選單系統 - 數據庫腳本
-- 創建時間: 2025-08-21
-- 版本: 1.0
-- 功能: 角色與選單權限管理系統
-- ==============================================

-- 選單表
CREATE TABLE IF NOT EXISTS menus (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    tree_path VARCHAR(255),
    name VARCHAR(64) NOT NULL,
    type SMALLINT NOT NULL, -- 0:目錄 1:選單 2:按鈕
    route_name VARCHAR(255),
    route_path VARCHAR(128),
    component VARCHAR(128),
    permission VARCHAR(128),
    always_show SMALLINT DEFAULT 0, -- 0:否 1:是
    keep_alive SMALLINT DEFAULT 0, -- 0:否 1:是  
    visible SMALLINT DEFAULT 1, -- 0:隱藏 1:顯示
    sort_order INTEGER DEFAULT 0,
    icon VARCHAR(64),
    redirect VARCHAR(128),
    params VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加外鍵約束 (自引用)
ALTER TABLE menus 
ADD CONSTRAINT fk_menus_parent 
FOREIGN KEY (parent_id) REFERENCES menus(id) ON DELETE SET NULL
DEFERRABLE INITIALLY DEFERRED;

-- 角色選單關聯表
CREATE TABLE IF NOT EXISTS role_menus (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_menu UNIQUE (role_id, menu_id),
    CONSTRAINT fk_role_menus_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_menus_menu FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
);

-- 創建索引
CREATE INDEX IF NOT EXISTS idx_menus_parent_id ON menus(parent_id);
CREATE INDEX IF NOT EXISTS idx_menus_type ON menus(type);
CREATE INDEX IF NOT EXISTS idx_menus_visible ON menus(visible);
CREATE INDEX IF NOT EXISTS idx_menus_sort_order ON menus(sort_order);
CREATE INDEX IF NOT EXISTS idx_role_menus_role_id ON role_menus(role_id);
CREATE INDEX IF NOT EXISTS idx_role_menus_menu_id ON role_menus(menu_id);

-- 添加更新時間觸發器
DROP TRIGGER IF EXISTS update_menus_updated_at ON menus;
CREATE TRIGGER update_menus_updated_at
    BEFORE UPDATE ON menus
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ==============================================
-- 初始化選單數據
-- ==============================================

-- 清空現有數據 (開發測試用)
-- DELETE FROM role_menus;
-- DELETE FROM menus;

-- 1. 主控台
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(1, 0, '/1', '主控台', 1, 'Dashboard', '/dashboard', 'views/dashboard/index', 'dashboard:view', 1, 1, 1, 1, 'Odometer', null)
ON CONFLICT (id) DO NOTHING;

-- 2. 系統管理 (目錄)
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(2, 0, '/2', '系統管理', 0, 'System', '/system', 'Layout', 'system:view', 1, 0, 1, 2, 'Setting', '/system/users')
ON CONFLICT (id) DO NOTHING;

-- 2.1 用戶管理
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(21, 2, '/2/21', '用戶管理', 1, 'Users', '/system/users', 'views/system/users/index', 'system:user:view', 0, 1, 1, 1, 'UserFilled', null)
ON CONFLICT (id) DO NOTHING;

-- 2.1.1 新增用戶
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(211, 21, '/2/21/211', '新增用戶', 2, null, null, null, 'system:user:add', 0, 0, 0, 1, null, null)
ON CONFLICT (id) DO NOTHING;

-- 2.1.2 編輯用戶
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(212, 21, '/2/21/212', '編輯用戶', 2, null, null, null, 'system:user:edit', 0, 0, 0, 2, null, null)
ON CONFLICT (id) DO NOTHING;

-- 2.1.3 刪除用戶
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(213, 21, '/2/21/213', '刪除用戶', 2, null, null, null, 'system:user:delete', 0, 0, 0, 3, null, null)
ON CONFLICT (id) DO NOTHING;

-- 2.2 角色管理
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(22, 2, '/2/22', '角色管理', 1, 'Roles', '/system/roles', 'views/system/roles/index', 'system:role:view', 0, 1, 1, 2, 'Avatar', null)
ON CONFLICT (id) DO NOTHING;

-- 2.2.1 新增角色
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(221, 22, '/2/22/221', '新增角色', 2, null, null, null, 'system:role:add', 0, 0, 0, 1, null, null)
ON CONFLICT (id) DO NOTHING;

-- 2.2.2 編輯角色
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(222, 22, '/2/22/222', '編輯角色', 2, null, null, null, 'system:role:edit', 0, 0, 0, 2, null, null)
ON CONFLICT (id) DO NOTHING;

-- 2.2.3 刪除角色
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(223, 22, '/2/22/223', '刪除角色', 2, null, null, null, 'system:role:delete', 0, 0, 0, 3, null, null)
ON CONFLICT (id) DO NOTHING;

-- 2.2.4 分配權限
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(224, 22, '/2/22/224', '分配權限', 2, null, null, null, 'system:role:assign', 0, 0, 0, 4, null, null)
ON CONFLICT (id) DO NOTHING;

-- 2.3 選單管理
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(23, 2, '/2/23', '選單管理', 1, 'Menus', '/system/menus', 'views/system/menus/index', 'system:menu:view', 0, 1, 1, 3, 'Menu', null)
ON CONFLICT (id) DO NOTHING;

-- 3. 日誌管理 (目錄)
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(3, 0, '/3', '日誌管理', 0, 'Logs', '/logs', 'Layout', 'logs:view', 0, 0, 1, 3, 'Document', '/logs/login')
ON CONFLICT (id) DO NOTHING;

-- 3.1 登入日誌
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(31, 3, '/3/31', '登入日誌', 1, 'LoginLogs', '/logs/login', 'views/logs/login/index', 'logs:login:view', 0, 1, 1, 1, 'Key', null)
ON CONFLICT (id) DO NOTHING;

-- 4. 個人中心
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(4, 0, '/4', '個人中心', 1, 'Profile', '/profile', 'views/profile/index', 'profile:view', 0, 1, 1, 4, 'User', null)
ON CONFLICT (id) DO NOTHING;

-- ==============================================
-- 初始化角色選單權限
-- ==============================================

-- 管理員(ADMIN)擁有所有選單權限
INSERT INTO role_menus (role_id, menu_id) 
SELECT r.id, m.id 
FROM roles r, menus m 
WHERE r.name = 'ADMIN'
ON CONFLICT (role_id, menu_id) DO NOTHING;

-- 普通用戶(USER)只能訪問主控台、個人中心、查看登入日誌
INSERT INTO role_menus (role_id, menu_id) 
SELECT r.id, m.id 
FROM roles r, menus m 
WHERE r.name = 'USER' 
AND m.id IN (1, 4, 31) -- 主控台、個人中心、登入日誌
ON CONFLICT (role_id, menu_id) DO NOTHING;

-- ==============================================
-- 重設序列值
-- ==============================================
SELECT setval('menus_id_seq', (SELECT MAX(id) FROM menus));

-- ==============================================
-- 驗證數據
-- ==============================================

-- 查看選單樹狀結構
WITH RECURSIVE menu_tree AS (
    -- 根選單
    SELECT 
        id, parent_id, name, type, route_path, permission, sort_order, 
        CAST(name AS VARCHAR(500)) as full_path,
        0 as level
    FROM menus 
    WHERE parent_id = 0
    
    UNION ALL
    
    -- 子選單
    SELECT 
        m.id, m.parent_id, m.name, m.type, m.route_path, m.permission, m.sort_order,
        CAST(mt.full_path || ' -> ' || m.name AS VARCHAR(500)) as full_path,
        mt.level + 1
    FROM menus m
    INNER JOIN menu_tree mt ON m.parent_id = mt.id
)
SELECT 
    REPEAT('  ', level) || name as menu_structure,
    type,
    route_path,
    permission
FROM menu_tree 
ORDER BY full_path;

-- 查看角色選單權限
SELECT 
    r.name as role_name,
    r.description,
    COUNT(rm.menu_id) as menu_count,
    array_agg(m.name ORDER BY m.sort_order) as menu_names
FROM roles r
LEFT JOIN role_menus rm ON r.id = rm.role_id
LEFT JOIN menus m ON rm.menu_id = m.id
GROUP BY r.id, r.name, r.description
ORDER BY r.name;

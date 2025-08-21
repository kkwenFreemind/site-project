-- 插入基本選單數據（簡化版本）

-- 清空現有數據
DELETE FROM role_menus;
DELETE FROM menus;

-- 主控台
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, created_at, updated_at) VALUES 
(1, NULL, '/1', 'Dashboard', 1, 'Dashboard', '/dashboard', 'views/dashboard/index', 'dashboard:view', 1, 1, 1, 1, 'Odometer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 系統管理目錄
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect, created_at, updated_at) VALUES 
(2, NULL, '/2', 'System', 0, 'System', '/system', 'Layout', 'system:view', 1, 0, 1, 2, 'Setting', '/system/users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 用戶管理
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, created_at, updated_at) VALUES 
(21, 2, '/2/21', 'Users', 1, 'Users', '/system/users', 'views/system/users/index', 'system:user:view', 0, 1, 1, 1, 'UserFilled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 角色管理
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, created_at, updated_at) VALUES 
(22, 2, '/2/22', 'Roles', 1, 'Roles', '/system/roles', 'views/system/roles/index', 'system:role:view', 0, 1, 1, 2, 'Avatar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 選單管理
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, created_at, updated_at) VALUES 
(23, 2, '/2/23', 'Menus', 1, 'Menus', '/system/menus', 'views/system/menus/index', 'system:menu:view', 0, 1, 1, 3, 'Menu', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 日誌管理目錄
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect, created_at, updated_at) VALUES 
(3, NULL, '/3', 'Logs', 0, 'Logs', '/logs', 'Layout', 'logs:view', 0, 0, 1, 3, 'Document', '/logs/login', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 登入日誌
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, created_at, updated_at) VALUES 
(31, 3, '/3/31', 'LoginLogs', 1, 'LoginLogs', '/logs/login', 'views/logs/login/index', 'logs:login:view', 0, 1, 1, 1, 'Key', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 個人中心
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, created_at, updated_at) VALUES 
(4, NULL, '/4', 'Profile', 1, 'Profile', '/profile', 'views/profile/index', 'profile:view', 0, 1, 1, 4, 'User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 為 ADMIN 角色分配所有選單權限
INSERT INTO role_menus (role_id, menu_id) 
SELECT r.id, m.id
FROM roles r, menus m 
WHERE r.name = 'ADMIN';

-- 為 USER 角色分配基本選單權限（主控台、個人中心、登入日誌）
INSERT INTO role_menus (role_id, menu_id) 
SELECT r.id, m.id
FROM roles r, menus m 
WHERE r.name = 'USER' 
AND m.id IN (1, 4, 31);

-- 重設序列值
SELECT setval('menus_id_seq', (SELECT MAX(id) FROM menus));

-- 驗證結果
SELECT 
    m.id, 
    m.name, 
    m.type, 
    m.route_path, 
    m.permission,
    CASE 
        WHEN m.type = 0 THEN 'Directory'
        WHEN m.type = 1 THEN 'Menu'
        WHEN m.type = 2 THEN 'Button'
        ELSE 'Unknown'
    END as type_desc
FROM menus m 
ORDER BY m.id;

-- 驗證角色權限分配
SELECT 
    r.name as role_name,
    COUNT(rm.menu_id) as menu_count
FROM roles r
LEFT JOIN role_menus rm ON r.id = rm.role_id
GROUP BY r.name
ORDER BY r.name;

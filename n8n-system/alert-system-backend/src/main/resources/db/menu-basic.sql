-- 基本選單系統初始化腳本

-- 創建選單表
CREATE TABLE IF NOT EXISTS menus (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    tree_path VARCHAR(255),
    name VARCHAR(64) NOT NULL,
    type SMALLINT NOT NULL,
    route_name VARCHAR(255),
    route_path VARCHAR(128),
    component VARCHAR(128),
    permission VARCHAR(128),
    always_show SMALLINT DEFAULT 0,
    keep_alive SMALLINT DEFAULT 0,  
    visible SMALLINT DEFAULT 1,
    sort_order INTEGER DEFAULT 0,
    icon VARCHAR(64),
    redirect VARCHAR(128),
    params VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 創建角色選單關聯表
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

-- 清空現有選單數據
DELETE FROM role_menus;
DELETE FROM menus;

-- 基本選單數據
INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(1, 0, '/1', 'Dashboard', 1, 'Dashboard', '/dashboard', 'views/dashboard/index', 'dashboard:view', 1, 1, 1, 1, 'Odometer', null);

INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(2, 0, '/2', 'System', 0, 'System', '/system', 'Layout', 'system:view', 1, 0, 1, 2, 'Setting', '/system/users');

INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(21, 2, '/2/21', 'Users', 1, 'Users', '/system/users', 'views/system/users/index', 'system:user:view', 0, 1, 1, 1, 'UserFilled', null);

INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(22, 2, '/2/22', 'Roles', 1, 'Roles', '/system/roles', 'views/system/roles/index', 'system:role:view', 0, 1, 1, 2, 'Avatar', null);

INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(23, 2, '/2/23', 'Menus', 1, 'Menus', '/system/menus', 'views/system/menus/index', 'system:menu:view', 0, 1, 1, 3, 'Menu', null);

INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(3, 0, '/3', 'Logs', 0, 'Logs', '/logs', 'Layout', 'logs:view', 0, 0, 1, 3, 'Document', '/logs/login');

INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(31, 3, '/3/31', 'LoginLogs', 1, 'LoginLogs', '/logs/login', 'views/logs/login/index', 'logs:login:view', 0, 1, 1, 1, 'Key', null);

INSERT INTO menus (id, parent_id, tree_path, name, type, route_name, route_path, component, permission, always_show, keep_alive, visible, sort_order, icon, redirect) VALUES 
(4, 0, '/4', 'Profile', 1, 'Profile', '/profile', 'views/profile/index', 'profile:view', 0, 1, 1, 4, 'User', null);

-- 管理員權限分配
INSERT INTO role_menus (role_id, menu_id) 
SELECT r.id, m.id 
FROM roles r, menus m 
WHERE r.name = 'ADMIN';

-- 普通用戶權限分配
INSERT INTO role_menus (role_id, menu_id) 
SELECT r.id, m.id 
FROM roles r, menus m 
WHERE r.name = 'USER' 
AND m.id IN (1, 4, 31);

-- 重設序列值
SELECT setval('menus_id_seq', (SELECT MAX(id) FROM menus));

-- 查看結果
SELECT id, name, type, route_path, permission FROM menus ORDER BY id;

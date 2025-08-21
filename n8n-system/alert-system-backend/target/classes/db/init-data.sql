-- Alert System Database Initialization Script
-- Created: 2025-08-21
-- Version: 1.0

-- ==============================================
-- User Authentication Tables
-- ==============================================

-- Insert default roles
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'System Administrator') 
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES 
('USER', 'Regular User') 
ON CONFLICT (name) DO NOTHING;

-- Create default admin account (password: admin123)
-- BCrypt encrypted password
INSERT INTO users (username, email, password_hash, full_name, status) VALUES 
('admin', 'admin@alertsystem.com', '$2a$10$HKVNMqVFGEfQ8YHWqBEzPeqYdE5qX9rJYYyGjEfPPmYzE9QSKVfpC', 'System Administrator', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- Assign admin role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Create test user (password: user123)
INSERT INTO users (username, email, password_hash, full_name, status) VALUES 
('testuser', 'user@alertsystem.com', '$2a$10$HKVNMqVFGEfQ8YHWqBEzPeqYdE5qX9rJYYyGjEfPPmYzE9QSKVfpC', 'Test User', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

-- Assign user role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'testuser' AND r.name = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Verify initial data
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

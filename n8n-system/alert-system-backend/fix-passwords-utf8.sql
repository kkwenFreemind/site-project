-- Fix user password hashes with correct BCrypt values
-- admin password: admin123
-- testuser password: test123

-- Clean existing data
DELETE FROM user_roles;
DELETE FROM users;

-- Insert users with correct password hashes
INSERT INTO users (username, email, password_hash, full_name, status, created_at, updated_at) VALUES
('admin', 'admin@example.com', '$2a$10$HYw7LzVtMzUWMCRI6E1QYuj6o27e25jStNEBfpXsTNmlz8wbZWtGG', 'System Administrator', 'ACTIVE', NOW(), NOW()),
('testuser', 'testuser@example.com', '$2a$10$1xBv/SL2wcUR0lG43zcWpuFEFm2sjxh8M/JBXdQvHWi2DevaLsvwC', 'Test User', 'ACTIVE', NOW(), NOW());

-- Set user role associations
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ADMIN')),
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'USER')),
((SELECT id FROM users WHERE username = 'testuser'), (SELECT id FROM roles WHERE name = 'USER'));

-- Verify the update results
SELECT username, LENGTH(password_hash) as hash_length, SUBSTRING(password_hash, 1, 7) as hash_prefix FROM users;

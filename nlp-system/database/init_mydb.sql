-- NLP Query System Database Setup for mydb schema
-- PostgreSQL Database Initialization Script

-- Connect to tsdb database and run the following:

-- Create mydb schema if not exists
CREATE SCHEMA IF NOT EXISTS mydb;

-- Set search path to mydb schema
SET search_path TO mydb;

-- Create AI Query Log table in mydb schema
CREATE TABLE IF NOT EXISTS mydb.ai_query_log (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100),
    user_query TEXT,
    generated_sql TEXT,
    executed_sql TEXT,
    status VARCHAR(20),
    result_count INTEGER,
    execution_time BIGINT,
    error_message TEXT,
    query_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    feedback VARCHAR(20),
    feedback_comment TEXT
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_ai_query_log_username ON mydb.ai_query_log(username);
CREATE INDEX IF NOT EXISTS idx_ai_query_log_status ON mydb.ai_query_log(status);
CREATE INDEX IF NOT EXISTS idx_ai_query_log_query_time ON mydb.ai_query_log(query_time);

-- Create sample tables for testing in mydb schema
CREATE TABLE IF NOT EXISTS mydb.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(200),
    full_name VARCHAR(200),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mydb.products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    category VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mydb.orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES mydb.users(id),
    product_id BIGINT REFERENCES mydb.products(id),
    quantity INTEGER DEFAULT 1,
    total_amount DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data for testing
INSERT INTO mydb.users (username, email, full_name, status) VALUES
('john_doe', 'john@example.com', 'John Doe', 'ACTIVE'),
('jane_smith', 'jane@example.com', 'Jane Smith', 'ACTIVE'),
('bob_wilson', 'bob@example.com', 'Bob Wilson', 'INACTIVE'),
('alice_brown', 'alice@example.com', 'Alice Brown', 'ACTIVE'),
('charlie_davis', 'charlie@example.com', 'Charlie Davis', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

INSERT INTO mydb.products (name, description, price, category, status) VALUES
('Laptop Computer', 'High-performance laptop for business use', 1299.99, 'Electronics', 'ACTIVE'),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 'Electronics', 'ACTIVE'),
('Office Chair', 'Comfortable office chair with lumbar support', 299.99, 'Furniture', 'ACTIVE'),
('Desk Lamp', 'LED desk lamp with adjustable brightness', 49.99, 'Furniture', 'ACTIVE'),
('Coffee Mug', 'Ceramic coffee mug with company logo', 12.99, 'Office Supplies', 'ACTIVE'),
('Notebook', 'Spiral notebook for meeting notes', 8.99, 'Office Supplies', 'ACTIVE'),
('Monitor Stand', 'Adjustable monitor stand for better ergonomics', 79.99, 'Electronics', 'INACTIVE')
ON CONFLICT DO NOTHING;

INSERT INTO mydb.orders (user_id, product_id, quantity, total_amount, status, order_date) VALUES
(1, 1, 1, 1299.99, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '10 days'),
(1, 2, 2, 59.98, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '8 days'),
(2, 3, 1, 299.99, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 4, 1, 49.99, 'PENDING', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(3, 5, 3, 38.97, 'CANCELLED', CURRENT_TIMESTAMP - INTERVAL '7 days'),
(4, 1, 1, 1299.99, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(4, 6, 5, 44.95, 'PROCESSING', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(5, 2, 1, 29.99, 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '4 days')
ON CONFLICT DO NOTHING;

-- Add comments to tables and columns for better AI understanding
COMMENT ON TABLE mydb.users IS '系統中的用戶帳戶';
COMMENT ON COLUMN mydb.users.username IS '用於登入的唯一用戶名';
COMMENT ON COLUMN mydb.users.email IS '用戶電子信箱地址';
COMMENT ON COLUMN mydb.users.full_name IS '用戶完整顯示名稱';
COMMENT ON COLUMN mydb.users.status IS '用戶帳戶狀態: ACTIVE活躍, INACTIVE非活躍';

COMMENT ON TABLE mydb.products IS '產品目錄';
COMMENT ON COLUMN mydb.products.name IS '產品名稱';
COMMENT ON COLUMN mydb.products.description IS '產品描述';
COMMENT ON COLUMN mydb.products.price IS '產品價格（美元）';
COMMENT ON COLUMN mydb.products.category IS '產品類別';
COMMENT ON COLUMN mydb.products.status IS '產品狀態: ACTIVE啟用, INACTIVE停用';

COMMENT ON TABLE mydb.orders IS '客戶訂單';
COMMENT ON COLUMN mydb.orders.user_id IS '參考 users 表';
COMMENT ON COLUMN mydb.orders.product_id IS '參考 products 表';
COMMENT ON COLUMN mydb.orders.quantity IS '訂購數量';
COMMENT ON COLUMN mydb.orders.total_amount IS '訂單總金額（美元）';
COMMENT ON COLUMN mydb.orders.status IS '訂單狀態: PENDING待處理, PROCESSING處理中, COMPLETED已完成, CANCELLED已取消';
COMMENT ON COLUMN mydb.orders.order_date IS '下訂單日期';

COMMENT ON TABLE mydb.ai_query_log IS 'AI自然語言查詢日誌';
COMMENT ON COLUMN mydb.ai_query_log.username IS '執行查詢的用戶';
COMMENT ON COLUMN mydb.ai_query_log.user_query IS '原始自然語言查詢';
COMMENT ON COLUMN mydb.ai_query_log.generated_sql IS 'AI生成的SQL';
COMMENT ON COLUMN mydb.ai_query_log.executed_sql IS '實際執行的SQL（含權限過濾）';
COMMENT ON COLUMN mydb.ai_query_log.status IS '查詢執行狀態: PROCESSING處理中, SUCCESS成功, ERROR錯誤';
COMMENT ON COLUMN mydb.ai_query_log.result_count IS '返回的行數';
COMMENT ON COLUMN mydb.ai_query_log.execution_time IS '查詢執行時間（毫秒）';
COMMENT ON COLUMN mydb.ai_query_log.feedback IS '用戶反饋: CORRECT正確, INCORRECT錯誤, PARTIAL部分正確';

-- Grant permissions to postgres user (already has all permissions)
-- No additional grants needed since postgres is the superuser

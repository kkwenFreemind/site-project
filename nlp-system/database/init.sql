-- NLP Query System Database Setup
-- PostgreSQL Database Initialization Script

-- Create database (run this as superuser)
-- CREATE DATABASE nlpquery WITH ENCODING 'UTF8';
-- CREATE USER nlpuser WITH PASSWORD 'nlppassword';
-- GRANT ALL PRIVILEGES ON DATABASE nlpquery TO nlpuser;

-- Connect to nlpquery database and run the following:

-- Create AI Query Log table
CREATE TABLE IF NOT EXISTS ai_query_log (
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
CREATE INDEX IF NOT EXISTS idx_ai_query_log_username ON ai_query_log(username);
CREATE INDEX IF NOT EXISTS idx_ai_query_log_status ON ai_query_log(status);
CREATE INDEX IF NOT EXISTS idx_ai_query_log_query_time ON ai_query_log(query_time);

-- Create sample tables for testing
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(200),
    full_name VARCHAR(200),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    category VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    product_id BIGINT REFERENCES products(id),
    quantity INTEGER DEFAULT 1,
    total_amount DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data for testing
INSERT INTO users (username, email, full_name, status) VALUES
('john_doe', 'john@example.com', 'John Doe', 'ACTIVE'),
('jane_smith', 'jane@example.com', 'Jane Smith', 'ACTIVE'),
('bob_wilson', 'bob@example.com', 'Bob Wilson', 'INACTIVE'),
('alice_brown', 'alice@example.com', 'Alice Brown', 'ACTIVE'),
('charlie_davis', 'charlie@example.com', 'Charlie Davis', 'ACTIVE')
ON CONFLICT (username) DO NOTHING;

INSERT INTO products (name, description, price, category, status) VALUES
('Laptop Computer', 'High-performance laptop for business use', 1299.99, 'Electronics', 'ACTIVE'),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 'Electronics', 'ACTIVE'),
('Office Chair', 'Comfortable office chair with lumbar support', 299.99, 'Furniture', 'ACTIVE'),
('Desk Lamp', 'LED desk lamp with adjustable brightness', 49.99, 'Furniture', 'ACTIVE'),
('Coffee Mug', 'Ceramic coffee mug with company logo', 12.99, 'Office Supplies', 'ACTIVE'),
('Notebook', 'Spiral notebook for meeting notes', 8.99, 'Office Supplies', 'ACTIVE'),
('Monitor Stand', 'Adjustable monitor stand for better ergonomics', 79.99, 'Electronics', 'INACTIVE')
ON CONFLICT DO NOTHING;

INSERT INTO orders (user_id, product_id, quantity, total_amount, status, order_date) VALUES
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
COMMENT ON TABLE users IS 'User accounts in the system';
COMMENT ON COLUMN users.username IS 'Unique username for login';
COMMENT ON COLUMN users.email IS 'User email address';
COMMENT ON COLUMN users.full_name IS 'User full display name';
COMMENT ON COLUMN users.status IS 'User account status: ACTIVE, INACTIVE';

COMMENT ON TABLE products IS 'Product catalog';
COMMENT ON COLUMN products.name IS 'Product name';
COMMENT ON COLUMN products.description IS 'Product description';
COMMENT ON COLUMN products.price IS 'Product price in USD';
COMMENT ON COLUMN products.category IS 'Product category';
COMMENT ON COLUMN products.status IS 'Product status: ACTIVE, INACTIVE';

COMMENT ON TABLE orders IS 'Customer orders';
COMMENT ON COLUMN orders.user_id IS 'Reference to users table';
COMMENT ON COLUMN orders.product_id IS 'Reference to products table';
COMMENT ON COLUMN orders.quantity IS 'Number of items ordered';
COMMENT ON COLUMN orders.total_amount IS 'Total order amount in USD';
COMMENT ON COLUMN orders.status IS 'Order status: PENDING, PROCESSING, COMPLETED, CANCELLED';
COMMENT ON COLUMN orders.order_date IS 'Date when order was placed';

COMMENT ON TABLE ai_query_log IS 'Log of AI natural language queries';
COMMENT ON COLUMN ai_query_log.username IS 'User who made the query';
COMMENT ON COLUMN ai_query_log.user_query IS 'Original natural language query';
COMMENT ON COLUMN ai_query_log.generated_sql IS 'SQL generated by AI';
COMMENT ON COLUMN ai_query_log.executed_sql IS 'Final SQL executed with permissions';
COMMENT ON COLUMN ai_query_log.status IS 'Query execution status: PROCESSING, SUCCESS, ERROR';
COMMENT ON COLUMN ai_query_log.result_count IS 'Number of rows returned';
COMMENT ON COLUMN ai_query_log.execution_time IS 'Query execution time in milliseconds';
COMMENT ON COLUMN ai_query_log.feedback IS 'User feedback: CORRECT, INCORRECT, PARTIAL';

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO nlpuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO nlpuser;

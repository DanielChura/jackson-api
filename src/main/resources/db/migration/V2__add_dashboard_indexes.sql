CREATE INDEX IF NOT EXISTS idx_orders_ordered_at ON orders (ordered_at);
CREATE INDEX IF NOT EXISTS idx_orders_status_ordered_at ON orders (status, ordered_at);
CREATE INDEX IF NOT EXISTS idx_order_details_product_id ON order_details (product_id);
CREATE INDEX IF NOT EXISTS idx_order_details_order_id ON order_details (order_id);
CREATE INDEX IF NOT EXISTS idx_products_is_active_stock ON products (is_active, stock);
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users (role_id);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users (created_at);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments (order_id);

CREATE TABLE brands (
    id uuid NOT NULL PRIMARY KEY,
    country_of_origin VARCHAR(100),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    description TEXT,
    is_active BOOLEAN,
    logo_url TEXT,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_brands_name UNIQUE (name)
);

CREATE TABLE categories (
    id uuid NOT NULL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    description VARCHAR(255),
    image_url TEXT,
    is_active BOOLEAN,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_categories_name UNIQUE (name)
);

CREATE TABLE roles (
    id uuid NOT NULL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    description VARCHAR(255),
    name VARCHAR(50) NOT NULL,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE users (
    id uuid NOT NULL PRIMARY KEY,
    address VARCHAR(255),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    email VARCHAR(150) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    password TEXT NOT NULL,
    phone VARCHAR(20),
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    role_id uuid REFERENCES roles(id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE products (
    id uuid NOT NULL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    description TEXT,
    is_active BOOLEAN NOT NULL,
    name VARCHAR(250) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    slug VARCHAR(250) NOT NULL,
    specifications JSONB,
    stock SMALLINT NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    brand_id uuid NOT NULL REFERENCES brands(id),
    category_id uuid NOT NULL REFERENCES categories(id),
    CONSTRAINT uq_products_name UNIQUE (name),
    CONSTRAINT uq_products_slug UNIQUE (slug)
);

CREATE TABLE carts (
    id uuid NOT NULL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    user_id uuid NOT NULL,
    CONSTRAINT uq_carts_user_id UNIQUE (user_id),
    CONSTRAINT fk_carts_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE orders (
    id uuid NOT NULL PRIMARY KEY,
    order_number BIGINT,
    ordered_at TIMESTAMP(6) WITHOUT TIME ZONE,
    shipping_address VARCHAR(255) NOT NULL,
    shipping_reference VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL,
    taxes NUMERIC(10,2) NOT NULL,
    total NUMERIC(10,2) NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT uq_orders_order_number UNIQUE (order_number),
    CONSTRAINT fk_orders_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_orders_status CHECK (status IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED'))
);

CREATE TABLE cart_items (
    id uuid NOT NULL PRIMARY KEY,
    added_at TIMESTAMP(6) WITHOUT TIME ZONE,
    quantity SMALLINT NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    cart_id uuid NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT uq_cart_items_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_items_product_id FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_cart_items_cart_id FOREIGN KEY (cart_id) REFERENCES carts(id)
);

CREATE TABLE order_details (
    id uuid NOT NULL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    quantity SMALLINT NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    order_id uuid NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT fk_order_details_product_id FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_order_details_order_id FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE payments (
    id uuid NOT NULL PRIMARY KEY,
    amount NUMERIC(10,2) NOT NULL,
    paid_at TIMESTAMP(6) WITHOUT TIME ZONE,
    payment_method VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    transaction_id uuid,
    order_id uuid NOT NULL,
    CONSTRAINT fk_payments_order_id FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT chk_payments_payment_method CHECK (payment_method IN ('PAYPAL', 'CASH', 'YAPE', 'PLIN', 'BANK_TRANSFER', 'DEBIT_CARD', 'CREDIT_CARD')),
    CONSTRAINT chk_payments_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);

CREATE TABLE product_images (
    id uuid NOT NULL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    display_order SMALLINT,
    url TEXT NOT NULL,
    product_id uuid NOT NULL,
    CONSTRAINT fk_product_images_product_id FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE inventory_movements (
    id uuid NOT NULL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    movement_type VARCHAR(255) NOT NULL,
    new_stock SMALLINT NOT NULL,
    previous_stock SMALLINT NOT NULL,
    quantity SMALLINT NOT NULL,
    reason VARCHAR(500),
    product_id uuid NOT NULL,
    CONSTRAINT fk_inventory_movements_product_id FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_inventory_movements_movement_type CHECK (movement_type IN ('IN', 'OUT', 'SALE', 'RETURN', 'ADJUSTMENT'))
);

CREATE TABLE favorites (
    id uuid NOT NULL PRIMARY KEY,
    added_at TIMESTAMP(6) WITHOUT TIME ZONE,
    product_id uuid NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT uq_favorites_user_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_favorites_product_id FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_favorites_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE reviews (
    id uuid NOT NULL PRIMARY KEY,
    comment VARCHAR(500),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    rating SMALLINT NOT NULL,
    product_id uuid NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT uq_reviews_user_product UNIQUE (user_id, product_id),
    CONSTRAINT fk_reviews_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_product_id FOREIGN KEY (product_id) REFERENCES products(id)
);

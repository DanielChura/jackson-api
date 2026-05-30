-- =========================================================
-- EXTENSIONS
-- =========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";



-- =========================================================
-- ENUMS
-- =========================================================

CREATE TYPE ORDER_STATUS AS ENUM(
	'PENDING',
	'PAID',
	'SHIPPED',
	'DELIVERED',
	'CANCELLED'
);

CREATE TYPE payment_method AS ENUM (
    'PAYPAL',
    'CASH',
    'YAPE',
    'PLIN',
    'BANK_TRANSFER',
    'DEBIT_CARD',
    'CREDIT_CARD'
);

CREATE TYPE payment_status AS ENUM (
    'PENDING',
    'COMPLETED',
    'FAILED',
    'REFUNDED'
);

CREATE TYPE movement_type AS ENUM (
    'IN',
    'OUT',
    'SALE',
    'RETURN',
    'ADJUSTMENT'
);



-- =========================================================
-- ROLES
-- =========================================================
-- Stores user roles like ADMIN or CUSTOMER
-- =========================================================

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- FIX:
    -- Removed invalid syntax:
    -- name role_name VARCHAR(50)
    name VARCHAR(50) NOT NULL UNIQUE,

    description VARCHAR(255),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- =========================================================
-- USERS
-- =========================================================
-- Main users table
-- =========================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    email VARCHAR(150) NOT NULL UNIQUE,

    -- Store encrypted password (BCrypt)
    password TEXT NOT NULL,

    -- FIX:
    -- phone should NOT be INT
    -- phone numbers are not used for calculations
    phone VARCHAR(20),

    address VARCHAR(255),

    role_id UUID NOT NULL,

    -- FIX:
    -- PostgreSQL uses TRUE/FALSE
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (role_id) REFERENCES roles(id)
);



-- =========================================================
-- CATEGORIES
-- =========================================================
-- Product categories
-- =========================================================

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL UNIQUE,

    description VARCHAR(255),

    image_url TEXT,

    -- FIX:
    -- was incorrectly TIMESTAMP
    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- =========================================================
-- BRANDS
-- =========================================================
-- Product brands
-- =========================================================

CREATE TABLE brands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL UNIQUE,

    description TEXT,

    logo_url TEXT,

    country_of_origin VARCHAR(100),

    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- =========================================================
-- PRODUCTS
-- =========================================================
-- Main products table
-- =========================================================
-- IMPORTANT:
-- specifications uses JSONB for flexible attributes
-- =========================================================

CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(250) NOT NULL UNIQUE,

    slug VARCHAR(250) NOT NULL UNIQUE,

    description TEXT,

    -- FIX:
    -- unitPrice should NEVER be INT
    -- NUMERIC is correct for money
    price NUMERIC(10,2) NOT NULL DEFAULT 0,

    stock SMALLINT NOT NULL DEFAULT 0,

    category_id UUID NOT NULL,
    brand_id UUID NOT NULL,

    -- Modern flexible product specifications
    specifications JSONB,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id)
        REFERENCES categories(id),

    FOREIGN KEY (brand_id)
        REFERENCES brands(id)
);



-- =========================================================
-- PRODUCT IMAGES
-- =========================================================
-- Multiple images per product
-- =========================================================

CREATE TABLE product_images (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    product_id UUID NOT NULL,

    url TEXT NOT NULL,

    display_order SMALLINT DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
);



-- =========================================================
-- CARTS
-- =========================================================
-- One active cart per user
-- =========================================================

CREATE TABLE carts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- FIX:
    -- user_id should NOT be PRIMARY KEY
    -- UNIQUE allows one cart per user
    user_id UUID NOT NULL UNIQUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);



-- =========================================================
-- CART ITEMS
-- =========================================================
-- Products inside carts
-- =========================================================

CREATE TABLE cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,

    quantity SMALLINT NOT NULL CHECK (quantity > 0),

    unit_price NUMERIC(10,2) NOT NULL,

    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (cart_id)
        REFERENCES carts(id)
        ON DELETE CASCADE,

    FOREIGN KEY (product_id)
        REFERENCES products(id),

    -- Prevent duplicated products in same cart
    UNIQUE(cart_id, product_id)
);



-- =========================================================
-- ORDERS
-- =========================================================
-- User purchases
-- =========================================================

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    -- FIX:
    -- AUTO_INCREMENT is MySQL syntax
    -- PostgreSQL uses BIGSERIAL
    order_number BIGSERIAL UNIQUE,

    subtotal NUMERIC(10,2) NOT NULL,

    taxes NUMERIC(10,2) NOT NULL DEFAULT 0,

    total NUMERIC(10,2) NOT NULL,

    shipping_address VARCHAR(255) NOT NULL,

    shipping_reference VARCHAR(255),

    status order_status NOT NULL DEFAULT 'PENDING',

    ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id)
);



-- =========================================================
-- ORDER DETAILS
-- =========================================================
-- Products inside orders
-- =========================================================

CREATE TABLE order_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    order_id UUID NOT NULL,

    product_id UUID NOT NULL,

    -- IMPORTANT:
    -- product_name is stored permanently
    -- so historical orders remain unchanged
    product_name VARCHAR(255) NOT NULL,

    quantity SMALLINT NOT NULL CHECK (quantity > 0),

    unit_price NUMERIC(10,2) NOT NULL,

    subtotal NUMERIC(10,2) NOT NULL,

    FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    FOREIGN KEY (product_id)
        REFERENCES products(id)
);



-- =========================================================
-- PAYMENTS
-- =========================================================
-- Payment records
-- =========================================================

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    order_id UUID NOT NULL,

    payment_method payment_method NOT NULL,

    amount NUMERIC(10,2) NOT NULL,

    status payment_status NOT NULL DEFAULT 'PENDING',

    transaction_id UUID,

    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);



-- =========================================================
-- FAVORITES
-- =========================================================
-- User favorite products
-- =========================================================

CREATE TABLE favorites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,
    product_id UUID NOT NULL,

    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    -- Prevent duplicate favorites
    UNIQUE(user_id, product_id)
);



-- =========================================================
-- REVIEWS
-- =========================================================
-- Product reviews and ratings
-- =========================================================

CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,
    product_id UUID NOT NULL,

    comment VARCHAR(500),

    -- FIX:
    -- rating validation
    rating SMALLINT NOT NULL
        CHECK (rating BETWEEN 1 AND 5),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    -- One review per user per product
    UNIQUE(user_id, product_id)
);



-- =========================================================
-- INVENTORY MOVEMENTS
-- =========================================================
-- Inventory history and stock tracking
-- =========================================================

CREATE TABLE inventory_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    product_id UUID NOT NULL,

    movement_type movement_type NOT NULL,

    quantity SMALLINT NOT NULL CHECK (quantity > 0),

    previous_stock SMALLINT NOT NULL,

    new_stock SMALLINT NOT NULL,

    reason VARCHAR(500),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id)
        REFERENCES products(id)
);

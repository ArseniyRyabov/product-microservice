--liquibase formatted sql

--changeset arseniyryabov:create-extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--changeset arseniyryabov:create-products
CREATE TABLE products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

--changeset arseniyryabov:create-products-indexes
CREATE INDEX idx_products_created_at ON products(created_at);
CREATE INDEX idx_products_updated_at ON products(updated_at);
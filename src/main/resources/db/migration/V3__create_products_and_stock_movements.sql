CREATE TABLE products (
    id             UUID          PRIMARY KEY,
    company_id     UUID          NOT NULL REFERENCES companies (id),
    sku            VARCHAR(50)   NOT NULL,
    name           VARCHAR(150)  NOT NULL,
    description    VARCHAR(500),
    unit           VARCHAR(10)   NOT NULL,
    cost_price     NUMERIC(14,2) NOT NULL DEFAULT 0,
    sale_price     NUMERIC(14,2) NOT NULL,
    stock_quantity NUMERIC(14,3) NOT NULL DEFAULT 0,
    minimum_stock  NUMERIC(14,3) NOT NULL DEFAULT 0,
    active         BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL,
    updated_at     TIMESTAMPTZ   NOT NULL,
    CONSTRAINT uk_products_company_sku UNIQUE (company_id, sku),
    CONSTRAINT ck_products_unit CHECK (unit IN ('UN', 'KG', 'G', 'L', 'ML', 'M', 'CX', 'PCT')),
    CONSTRAINT ck_products_cost_price CHECK (cost_price >= 0),
    CONSTRAINT ck_products_sale_price CHECK (sale_price >= 0),
    CONSTRAINT ck_products_stock_quantity CHECK (stock_quantity >= 0),
    CONSTRAINT ck_products_minimum_stock CHECK (minimum_stock >= 0)
);

CREATE TABLE stock_movements (
    id            UUID          PRIMARY KEY,
    company_id    UUID          NOT NULL REFERENCES companies (id),
    product_id    UUID          NOT NULL REFERENCES products (id),
    movement_type VARCHAR(20)   NOT NULL,
    quantity      NUMERIC(14,3) NOT NULL,
    balance_after NUMERIC(14,3) NOT NULL,
    reason        VARCHAR(255),
    created_at    TIMESTAMPTZ   NOT NULL,
    updated_at    TIMESTAMPTZ   NOT NULL,
    CONSTRAINT ck_stock_movements_type CHECK (movement_type IN ('ENTRY', 'EXIT', 'ADJUSTMENT')),
    CONSTRAINT ck_stock_movements_balance_after CHECK (balance_after >= 0)
);

CREATE INDEX idx_stock_movements_product_created ON stock_movements (product_id, created_at DESC);
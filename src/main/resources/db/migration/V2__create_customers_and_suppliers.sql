CREATE TABLE customers (
    id          UUID         PRIMARY KEY,
    company_id  UUID         NOT NULL REFERENCES companies (id),
    name        VARCHAR(150) NOT NULL,
    document    VARCHAR(14),
    email       VARCHAR(150),
    phone       VARCHAR(30),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_customers_company_document UNIQUE (company_id, document)
);

CREATE TABLE suppliers (
    id          UUID         PRIMARY KEY,
    company_id  UUID         NOT NULL REFERENCES companies (id),
    name        VARCHAR(150) NOT NULL,
    document    VARCHAR(14),
    email       VARCHAR(150),
    phone       VARCHAR(30),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_suppliers_company_document UNIQUE (company_id, document)
);
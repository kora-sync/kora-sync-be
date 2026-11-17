CREATE TABLE companies (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    legal_name  VARCHAR(200),
    tax_id      VARCHAR(14)  NOT NULL,
    email       VARCHAR(150),
    phone       VARCHAR(30),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX uk_companies_tax_id ON companies (tax_id);
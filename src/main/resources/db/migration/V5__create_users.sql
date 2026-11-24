CREATE TABLE users (
                       id            UUID         PRIMARY KEY,
                       company_id    UUID         NOT NULL REFERENCES companies (id),
                       name          VARCHAR(150) NOT NULL,
                       email         VARCHAR(254) NOT NULL,
                       password_hash VARCHAR(100) NOT NULL,
                       role          VARCHAR(20)  NOT NULL,
                       active        BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at    TIMESTAMPTZ  NOT NULL,
                       updated_at    TIMESTAMPTZ  NOT NULL,
                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT ck_users_email_lowercase CHECK (email = lower(email)),
                       CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'MANAGER', 'OPERATOR'))
);

CREATE INDEX idx_users_company ON users (company_id);
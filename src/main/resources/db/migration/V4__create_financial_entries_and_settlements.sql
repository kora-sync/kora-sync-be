CREATE TABLE financial_entries (
                                   id             UUID          PRIMARY KEY,
                                   company_id     UUID          NOT NULL REFERENCES companies (id),
                                   entry_type     VARCHAR(20)   NOT NULL,
                                   customer_id    UUID          REFERENCES customers (id),
                                   supplier_id    UUID          REFERENCES suppliers (id),
                                   description    VARCHAR(255)  NOT NULL,
                                   amount         NUMERIC(14,2) NOT NULL,
                                   settled_amount NUMERIC(14,2) NOT NULL DEFAULT 0,
                                   due_date       DATE          NOT NULL,
                                   status         VARCHAR(20)   NOT NULL,
                                   created_at     TIMESTAMPTZ   NOT NULL,
                                   updated_at     TIMESTAMPTZ   NOT NULL,
                                   CONSTRAINT ck_financial_entries_type CHECK (entry_type IN ('RECEIVABLE', 'PAYABLE')),
                                   CONSTRAINT ck_financial_entries_status CHECK (status IN ('OPEN', 'PARTIALLY_SETTLED', 'SETTLED', 'CANCELED')),
                                   CONSTRAINT ck_financial_entries_amount CHECK (amount > 0),
                                   CONSTRAINT ck_financial_entries_settled CHECK (settled_amount >= 0 AND settled_amount <= amount),
                                   CONSTRAINT ck_financial_entries_partner CHECK (
                                       (entry_type = 'RECEIVABLE' AND supplier_id IS NULL)
                                           OR (entry_type = 'PAYABLE' AND customer_id IS NULL))
);

CREATE INDEX idx_financial_entries_company_due ON financial_entries (company_id, due_date);
CREATE INDEX idx_financial_entries_company_type_status ON financial_entries (company_id, entry_type, status);

CREATE TABLE settlements (
                             id             UUID          PRIMARY KEY,
                             company_id     UUID          NOT NULL REFERENCES companies (id),
                             entry_id       UUID          NOT NULL REFERENCES financial_entries (id),
                             amount         NUMERIC(14,2) NOT NULL,
                             settled_on     DATE          NOT NULL,
                             payment_method VARCHAR(20)   NOT NULL,
                             notes          VARCHAR(255),
                             created_at     TIMESTAMPTZ   NOT NULL,
                             updated_at     TIMESTAMPTZ   NOT NULL,
                             CONSTRAINT ck_settlements_amount CHECK (amount > 0),
                             CONSTRAINT ck_settlements_method CHECK (
                                 payment_method IN ('CASH', 'PIX', 'CREDIT_CARD', 'DEBIT_CARD', 'BANK_SLIP', 'BANK_TRANSFER'))
);

CREATE INDEX idx_settlements_entry ON settlements (entry_id);
CREATE INDEX idx_settlements_company_settled_on ON settlements (company_id, settled_on);
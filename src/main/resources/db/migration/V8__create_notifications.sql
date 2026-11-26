CREATE TABLE notifications (
                               id           UUID         PRIMARY KEY,
                               company_id   UUID         NOT NULL REFERENCES companies (id),
                               type         VARCHAR(30)  NOT NULL,
                               title        VARCHAR(150) NOT NULL,
                               message      VARCHAR(500) NOT NULL,
                               reference_id UUID,
                               dedupe_key   VARCHAR(150) NOT NULL,
                               read_at      TIMESTAMPTZ,
                               read_by      UUID         REFERENCES users (id),
                               created_at   TIMESTAMPTZ  NOT NULL,
                               updated_at   TIMESTAMPTZ  NOT NULL,
                               CONSTRAINT uk_notifications_company_dedupe UNIQUE (company_id, dedupe_key),
                               CONSTRAINT ck_notifications_type CHECK (type IN ('LOW_STOCK', 'ENTRY_DUE_SOON', 'ENTRY_OVERDUE'))
);

CREATE INDEX idx_notifications_company_created ON notifications (company_id, created_at DESC);
CREATE INDEX idx_notifications_company_unread ON notifications (company_id) WHERE read_at IS NULL;
ALTER TABLE stock_movements ADD COLUMN created_by UUID REFERENCES users (id);
ALTER TABLE settlements     ADD COLUMN created_by UUID REFERENCES users (id);
ALTER TABLE payments ADD CONSTRAINT uq_payments_transaction_id UNIQUE (transaction_id);

CREATE SEQUENCE IF NOT EXISTS order_number_seq START WITH 1 INCREMENT BY 1;

--liquibase formatted sql

--changeset task#71:1
ALTER TABLE entries ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE entries ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone NULL;
ALTER TABLE entries ADD COLUMN IF NOT EXISTS operation_id SERIAL;
ALTER TABLE bounty ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE bounty ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone NULL;
ALTER TABLE bounty ADD COLUMN IF NOT EXISTS operation_id SERIAL;
ALTER TABLE withdrawal ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE withdrawal ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone NULL;
ALTER TABLE withdrawal ADD COLUMN IF NOT EXISTS operation_id SERIAL;

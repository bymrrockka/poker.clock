--liquibase formatted sql

--changeset task#13:1
ALTER TABLE bounty ADD COLUMN IF NOT EXISTS amount bigint NOT NULL;
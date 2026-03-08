--liquibase formatted sql

--changeset task#71:1
ALTER TABLE person DROP COLUMN IF EXISTS last_name;
ALTER TABLE person DROP COLUMN IF EXISTS first_name;

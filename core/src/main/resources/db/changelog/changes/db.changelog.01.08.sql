--liquibase formatted sql

--changeset task#45:1
ALTER TABLE game
    ALTER COLUMN bounty DROP NOT NULL;

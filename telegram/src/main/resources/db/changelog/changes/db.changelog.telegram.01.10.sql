--liquibase formatted sql

--changeset task#51:1
CREATE TABLE IF NOT EXISTS game_tables (
    game_id uuid REFERENCES game(id),
    tables jsonb NOT NULL,

    UNIQUE(game_id)
);

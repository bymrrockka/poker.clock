--liquibase formatted sql

--changeset task#51:1
CREATE TABLE IF NOT EXISTS game_seats (
    game_id uuid REFERENCES game(id),
    seats jsonb NOT NULL,

    UNIQUE(game_id)
);

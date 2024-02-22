--liquibase formatted sql

--changeset task#4:2

CREATE TABLE IF NOT EXISTS chat_games (
    game_id uuid REFERENCES game(id),
    chat_id bigint NOT NULL,
    created_at timestamp with time zone NOT NULL,
	UNIQUE(game_id, chat_id)
);

CREATE TABLE IF NOT EXISTS chat_persons (
    person_id uuid REFERENCES person(id),
    chat_id bigint NOT NULL,
	telegram varchar(50),
    UNIQUE(telegram, chat_id)
);
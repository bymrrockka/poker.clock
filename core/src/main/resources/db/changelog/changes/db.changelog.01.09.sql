--liquibase formatted sql

--changeset task#63:1
CREATE TABLE IF NOT EXISTS game_summary (
    game_id uuid REFERENCES game(id),
    person_id uuid REFERENCES person(id),
    position int,
    amount bigint NOT NULL,
    type varchar(20) NOT NULL,
    UNIQUE(game_id, person_id),
    UNIQUE(game_id, position)
    );

--changeset task#63:2
DROP TABLE IF EXISTS money_transfer;

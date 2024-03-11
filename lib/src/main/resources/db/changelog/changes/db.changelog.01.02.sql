--liquibase formatted sql

--changeset task#11:1
ALTER TABLE person ADD COLUMN IF NOT EXISTS nick_name varchar(32);

UPDATE game SET bounty = 0 WHERE bounty IS NULL;

ALTER TABLE game ALTER COLUMN bounty SET NOT NULL;

CREATE TABLE IF NOT EXISTS withdrawal (
	game_id uuid REFERENCES game(id),
    person_id uuid REFERENCES person(id),
    amount bigint NOT NULL,
    created_at timestamp with time zone NOT NULL
);

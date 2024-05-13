--liquibase formatted sql

--changeset task#36:1
CREATE TABLE IF NOT EXISTS money_transfer (
    game_id uuid REFERENCES game(id),
	person_id uuid REFERENCES person(id),
    amount bigint NOT NULL,
    type varchar(6) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
	UNIQUE(game_id, person_id)
);

ALTER TABLE game ADD COLUMN IF NOT EXISTS created_at timestamp with time zone NULL;
UPDATE game SET created_at = NOW() WHERE created_at IS NULL;
ALTER TABLE game ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE game ADD COLUMN IF NOT EXISTS finished_at timestamp with time zone NULL;
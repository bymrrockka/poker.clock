--liquibase formatted sql

--changeset task#4:1

CREATE TABLE IF NOT EXISTS game (
    id uuid PRIMARY KEY,
    chat_id varchar(50) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    stack bigint NOT NULL,
    buy_in bigint NOT NULL,
    bounty bigint,
    game_type varchar(10) NOT NULL,
    is_deleted boolean,
    UNIQUE(id, chat_id)
);

CREATE TABLE IF NOT EXISTS person (
	id uuid PRIMARY KEY,
	chat_id varchar(50) NOT NULL,
	telegram varchar(100) NOT NULL,
	first_name varchar(100),
	last_name varchar(100),
    UNIQUE(chat_id, telegram)
);

CREATE TABLE IF NOT EXISTS prize_pool (
	game_id uuid UNIQUE REFERENCES game(id),
	schema jsonb NOT NULL
);

CREATE TABLE IF NOT EXISTS entry (
	game_id uuid REFERENCES game(id),
	person_id uuid REFERENCES person(id),
	amount bigint NOT NULL,
	created_at timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS bounty (
	game_id uuid REFERENCES game(id),
	from_person uuid REFERENCES person(id),
	to_person uuid REFERENCES person(id),
    created_at timestamp with time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS finale_places (
	game_id uuid REFERENCES game(id),
	person_id uuid REFERENCES person(id),
	place int NOT NULL,
	UNIQUE(game_id, person_id),
	UNIQUE(game_id, place)
);
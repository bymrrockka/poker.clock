--liquibase formatted sql

--changeset task#4:1

CREATE TABLE IF NOT EXISTS game (
    id uuid NOT NULL,
    chat_id varchar(50) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    stack bigint NOT NULL,
    buy_in bigint NOT NULL,
    bounty bigint,
    game_type varchar(10) NOT NULL,
    is_deleted boolean,
    PRIMARY KEY (id, chat_id)
);

CREATE TABLE IF NOT EXISTS person (
	id uuid NOT NULL,
	chat_id varchar(50) NOT NULL,
	telegram varchar(100) NOT NULL,
	first_name varchar(100),
	last_name varchar(100),
  PRIMARY KEY (id, chat_id)
);

CREATE TABLE IF NOT EXISTS prize_pool (
	game_id uuid NOT NULL,
	schema jsonb NOT NULL,
	PRIMARY KEY (game_id),
  CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES game(game_id)
);

CREATE TABLE IF NOT EXISTS entry (
	game_id uuid NOT NULL,
	person_id uuid NOT NULL,
	amount bigint NOT NULL,
	created_at timestamp with time zone NOT NULL,
  CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES game(game_id),
  CONSTRAINT fk_person FOREIGN KEY (person_id) REFERENCES person(person_id)
);

CREATE TABLE IF NOT EXISTS bounty (
	game_id uuid NOT NULL,
	from uuid NOT NULL,
	to uuid NOT NULL,
	amount bigint,
  created_at timestamp with time zone NOT NULL,
  CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES game(game_id),
  CONSTRAINT fk_to_person FOREIGN KEY (to_person) REFERENCES person(person_id),
  CONSTRAINT fk_from_person FOREIGN KEY (from_person) REFERENCES person(person_id)
);

CREATE TABLE IF NOT EXISTS final_places (
	game_id uuid NOT NULL,
	places jsonb NOT NULL,
	PRIMARY KEY (game_id),
  CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES game(game_id)
);
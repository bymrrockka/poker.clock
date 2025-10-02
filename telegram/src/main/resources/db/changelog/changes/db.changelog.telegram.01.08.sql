--liquibase formatted sql

--changeset task#45:4
CREATE TABLE IF NOT EXISTS chat_polls (
    poll_id uuid REFERENCES poll_task(id),
    telegram_poll_id varchar(50),
    UNIQUE(telegram_poll_id, poll_id)
);

CREATE INDEX IF NOT EXISTS chat_tg_poll_id
    ON chat_polls (telegram_poll_id);

CREATE TABLE IF NOT EXISTS poll_answers (
    telegram_poll_id varchar(50),
    person_id uuid REFERENCES person(id),
    answer INT NOT NULL,
    UNIQUE(telegram_poll_id, person_id)
);

CREATE INDEX IF NOT EXISTS poll_answers_poll_id
    ON poll_answers (telegram_poll_id);

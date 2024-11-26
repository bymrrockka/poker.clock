--liquibase formatted sql

--changeset task#43:1
CREATE TABLE IF NOT EXISTS poll_task
(
    id          uuid PRIMARY KEY,
    chat_id     bigint                   NOT NULL,
    message_id  int                      NOT NULL,
    cron        varchar(20)              NOT NULL,
    message     varchar(255)             NOT NULL,
    options     jsonb                    NOT NULL,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    finished_at timestamp with time zone,
    UNIQUE (message_id, chat_id)
);
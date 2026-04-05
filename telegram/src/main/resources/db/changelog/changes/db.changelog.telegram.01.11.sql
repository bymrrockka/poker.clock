--liquibase formatted sql

--changeset task#78:1
CREATE TABLE IF NOT EXISTS chat_messages (
    chat_id bigint NOT NULL,
    message_id bigint NOT NULL,
    operation_id integer NOT NULL,
    type varchar(10) NOT NULL,
    created_at timestamp with time zone NOT NULL,

    PRIMARY KEY (chat_id, message_id)
);
--liquibase formatted sql

--changeset task#78:1
CREATE TABLE IF NOT EXISTS chat_messages (
    chat_id bigint NOT NULL,
    message_id bigint NOT NULL,
    type varchar(10) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,

    PRIMARY KEY (chat_id, message_id)
);

--liquibase formatted sql

--changeset task#54:1
CREATE TABLE IF NOT EXISTS pin_messages (
    chat_id bigint NOT NULL,
    message_id bigint NOT NULL,
    type varchar(15) NOT NULL,
    UNIQUE(chat_id, message_id)
);

CREATE INDEX pin_messages_chat_id_type_index ON pin_messages (chat_id, type);
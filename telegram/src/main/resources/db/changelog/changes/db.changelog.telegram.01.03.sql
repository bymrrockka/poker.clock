--liquibase formatted sql

--changeset task#11:2
ALTER TABLE chat_persons ADD CONSTRAINT chat_persons_person_id_chat_id_key UNIQUE (person_id, chat_id);
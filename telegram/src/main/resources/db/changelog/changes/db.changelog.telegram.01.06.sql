--liquibase formatted sql

--changeset task#45:1
CREATE UNIQUE INDEX IF NOT EXISTS chat_persons_person_to_chat_index
    ON chat_persons (person_id, chat_id);

ALTER TABLE chat_persons DROP CONSTRAINT chat_persons_person_id_chat_id_key;
ALTER TABLE chat_persons
    ADD CONSTRAINT chat_persons_person_id_chat_id_key UNIQUE USING INDEX chat_persons_person_to_chat_index;

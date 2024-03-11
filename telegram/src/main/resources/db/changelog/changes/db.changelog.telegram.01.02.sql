--liquibase formatted sql

--changeset task#11:2
UPDATE person as p
SET nick_name = telegram
FROM chat_persons as cp
WHERE cp.person_id = p.id;

ALTER TABLE chat_persons DROP CONSTRAINT chat_persons_telegram_chat_id_key;
ALTER TABLE chat_persons DROP COLUMN telegram;
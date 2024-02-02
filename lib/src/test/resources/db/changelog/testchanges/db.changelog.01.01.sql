--liquibase formatted sql

--changeset task#4:1
insert into person (id, chat_id, telegram, first_name, last_name)
values  (gen_random_uuid(), gen_random_uuid()::text, 'jack', 'Grisha', 'Anikii'),
        (gen_random_uuid(), gen_random_uuid()::text, 'queen', 'Kate', null),
        (gen_random_uuid(), gen_random_uuid()::text, 'king', null, 'Portugal'),
        (gen_random_uuid(), gen_random_uuid()::text, 'ace', null, null);

--liquibase formatted sql

--changeset task#4:2

insert into game (id, chat_id, created_at, stack, buy_in, game_type)
values  (gen_random_uuid(), gen_random_uuid()::text, NOW(), 1000, 15, 'TOURNAMENT'),
        (gen_random_uuid(), gen_random_uuid()::text, NOW(), 1500, 30, 'TOURNAMENT'),
        (gen_random_uuid(), gen_random_uuid()::text, NOW(), 30000, 30, 'TOURNAMENT'),
        (gen_random_uuid(), gen_random_uuid()::text, NOW(), 10000, 15, 'TOURNAMENT');

insert into person (id, chat_id, telegram, first_name, last_name)
values  (gen_random_uuid(), gen_random_uuid()::text, 'jack', 'Grisha', 'Anikii'),
        (gen_random_uuid(), gen_random_uuid()::text, 'queen', 'Kate', null),
        (gen_random_uuid(), gen_random_uuid()::text, 'king', null, 'Portugal'),
        (gen_random_uuid(), gen_random_uuid()::text, 'ace', null, null);

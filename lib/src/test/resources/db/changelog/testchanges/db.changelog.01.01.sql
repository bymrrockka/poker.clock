--liquibase formatted sql

--changeset task#4:1
insert into person (id, chat_id, telegram, first_name, last_name)
values  (gen_random_uuid(), '123', 'jack', 'Grisha', 'Anikii'),
        (gen_random_uuid(), '123', 'queen', 'Kate', null),
        (gen_random_uuid(), '123', 'king', null, 'Portugal'),
        (gen_random_uuid(), '123', 'ace', null, null);


insert into game (id, chat_id, created_at, stack, buy_in, bounty, game_type)
values  ('fa3d03c4-f411-4852-810f-c0cc2f5b8c84', '123', NOW(), 1000, 15, null, 'TOURNAMENT'),
        ('4a411a12-2386-4dce-b579-d806c91d6d17', '123', NOW(), 1500, 30, null, 'TOURNAMENT'),
        ('3e255b72-db57-4cc8-9c07-56991a8ab67a', '123', NOW(), 30000, 30, 30, 'TOURNAMENT'),
        ('b759ac52-1496-463f-b0d8-982deeac085c', '123', NOW(), 10000, 15, 25, 'TOURNAMENT');

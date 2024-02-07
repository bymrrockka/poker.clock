--liquibase formatted sql

--changeset task#4:1
insert into person (id, chat_id, telegram, first_name, last_name)
values  ('13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', '123', 'jack', 'Grisha', 'Anikii'),
        ('72775968-3da6-469e-8a61-60104eacdb3a', '123', 'queen', 'Kate', null),
        ('e2691144-3b1b-4841-9693-fad7af25bba9', '123', 'king', null, 'Portugal'),
        ('58ae9984-1ebc-4621-ba0e-a577c69283ef', '123', 'ace', null, null);


insert into game (id, chat_id, created_at, stack, buy_in, bounty, game_type)
values  ('fa3d03c4-f411-4852-810f-c0cc2f5b8c84', '123', NOW(), 1000, 15, null, 'TOURNAMENT'),
        ('4a411a12-2386-4dce-b579-d806c91d6d17', '123', NOW(), 1500, 30, null, 'TOURNAMENT'),
        ('3e255b72-db57-4cc8-9c07-56991a8ab67a', '123', NOW(), 30000, 30, 30, 'TOURNAMENT'),
        ('b759ac52-1496-463f-b0d8-982deeac085c', '123', NOW(), 10000, 15, 25, 'TOURNAMENT');

insert into finale_places (game_id, place, person_id)
values ('fa3d03c4-f411-4852-810f-c0cc2f5b8c84', 1, '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8'),
       ('fa3d03c4-f411-4852-810f-c0cc2f5b8c84', 2, '72775968-3da6-469e-8a61-60104eacdb3a');
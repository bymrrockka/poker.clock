--liquibase formatted sql

--changeset task#4:1
insert into person (id, first_name, last_name, nick_name)
values  ('13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 'Grisha', 'Anikii', 'kinger'),
        ('72775968-3da6-469e-8a61-60104eacdb3a', 'Kate', null, 'queen'),
        ('e2691144-3b1b-4841-9693-fad7af25bba9', null, 'Portugal', 'jackas'),
        ('58ae9984-1ebc-4621-ba0e-a577c69283ef', null, null, 'tenten'),
        ('2d5cdad1-4a75-48f4-9d7f-a2497d86a902', null, null, 'nines'),
        ('66528684-6c96-4f80-b4e6-02d734389107', null, null, 'eights'),
        ('7d880b10-eed3-4f98-a86d-5b1ed7adc7b4', null, null, 'seventh');

insert into game (id, stack, buy_in, bounty, game_type, created_at)
values  ('fa3d03c4-f411-4852-810f-c0cc2f5b8c84', 1000, 15, 0, 'CASH', NOW()),
        ('4a411a12-2386-4dce-b579-d806c91d6d17', 1500, 30, 0, 'TOURNAMENT', NOW()),
        ('3e255b72-db57-4cc8-9c07-56991a8ab67a', 30000, 30, 0, 'TOURNAMENT', NOW()),
        ('b759ac52-1496-463f-b0d8-982deeac085c', 10000, 15, 0, 'TOURNAMENT', NOW());

insert into chat_persons (person_id, chat_id)
values  ('13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 123),
        ('72775968-3da6-469e-8a61-60104eacdb3a', 123),
        ('e2691144-3b1b-4841-9693-fad7af25bba9', 123),
        ('58ae9984-1ebc-4621-ba0e-a577c69283ef', 123),
        ('7d880b10-eed3-4f98-a86d-5b1ed7adc7b4', 12),
        ('66528684-6c96-4f80-b4e6-02d734389107', 12);

insert into chat_games (game_id, chat_id, created_at, message_id)
values  ('4a411a12-2386-4dce-b579-d806c91d6d17', 123, NOW(), 1),
        ('b759ac52-1496-463f-b0d8-982deeac085c', 123, NOW(), 2);

insert into entries (game_id, person_id, amount, created_at)
values  ('4a411a12-2386-4dce-b579-d806c91d6d17', '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 30000, NOW()),
        ('4a411a12-2386-4dce-b579-d806c91d6d17', '72775968-3da6-469e-8a61-60104eacdb3a', 30000, NOW());

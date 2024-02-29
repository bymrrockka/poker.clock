--liquibase formatted sql

--changeset task#4:1
insert into person (id, first_name, last_name)
values  ('13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 'Grisha', 'Anikii'),
        ('72775968-3da6-469e-8a61-60104eacdb3a', 'Kate', null),
        ('e2691144-3b1b-4841-9693-fad7af25bba9', null, 'Portugal'),
        ('58ae9984-1ebc-4621-ba0e-a577c69283ef', null, null);


insert into game (id, stack, buy_in, bounty, game_type)
values  ('fa3d03c4-f411-4852-810f-c0cc2f5b8c84', 1000, 15, null, 'TOURNAMENT'),
        ('4a411a12-2386-4dce-b579-d806c91d6d17', 1500, 30, null, 'BOUNTY'),
        ('3e255b72-db57-4cc8-9c07-56991a8ab67a', 30000, 30, 30, 'CASH'),
        ('b759ac52-1496-463f-b0d8-982deeac085c', 10000, 15, 25, 'TOURNAMENT');

insert into chat_persons (person_id, chat_id, telegram)
values  ('13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 123, 'king'),
        ('72775968-3da6-469e-8a61-60104eacdb3a', 123, 'queen'),
        ('e2691144-3b1b-4841-9693-fad7af25bba9', 123, 'jack'),
        ('58ae9984-1ebc-4621-ba0e-a577c69283ef', 123, 'ten');

insert into chat_games (game_id, chat_id, created_at)
values  ('4a411a12-2386-4dce-b579-d806c91d6d17', 123, '2024-01-01 00:00:01 +00:00');

insert into entries (game_id, person_id, amount, created_at)
values  ('4a411a12-2386-4dce-b579-d806c91d6d17', '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 30000, NOW()),
        ('4a411a12-2386-4dce-b579-d806c91d6d17', '72775968-3da6-469e-8a61-60104eacdb3a', 30000, NOW());

-- calculate int tests
insert into chat_games (game_id, chat_id, created_at)
values  ('3e255b72-db57-4cc8-9c07-56991a8ab67a', 123, '2024-01-02 00:00:01 +00:00');

insert into entries (game_id, person_id, amount, created_at)
values  ('3e255b72-db57-4cc8-9c07-56991a8ab67a', '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 30, NOW()),
        ('3e255b72-db57-4cc8-9c07-56991a8ab67a', '72775968-3da6-469e-8a61-60104eacdb3a', 30, NOW()),
        ('3e255b72-db57-4cc8-9c07-56991a8ab67a', 'e2691144-3b1b-4841-9693-fad7af25bba9', 30, NOW()),
        ('3e255b72-db57-4cc8-9c07-56991a8ab67a', '58ae9984-1ebc-4621-ba0e-a577c69283ef', 30, NOW());

insert into prize_pool (game_id, schema)
values  ('3e255b72-db57-4cc8-9c07-56991a8ab67a', '{"1": 70,"2": 30}'::jsonb);

insert into finale_places (game_id, position, person_id)
values  ('3e255b72-db57-4cc8-9c07-56991a8ab67a', 1, '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8'),
        ('3e255b72-db57-4cc8-9c07-56991a8ab67a', 2, '72775968-3da6-469e-8a61-60104eacdb3a');
--liquibase formatted sql

--changeset task#6:1
--calculate tournament tests
insert into chat_games (game_id, chat_id, created_at, message_id)
values  ('3e255b72-db57-4cc8-9c07-56991a8ab67a', 123, NOW(), 3);

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
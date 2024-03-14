--liquibase formatted sql

--changeset task#9:1
--calculate cash tests
insert into game (id, stack, buy_in, bounty, game_type)
values  ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', 1000, 30, 0, 'CASH');

insert into chat_games (game_id, chat_id, created_at, message_id)
values  ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', 123, NOW(), 4);

insert into entries (game_id, person_id, amount, created_at)
values  ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 30, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '72775968-3da6-469e-8a61-60104eacdb3a', 30, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', 'e2691144-3b1b-4841-9693-fad7af25bba9', 30, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '58ae9984-1ebc-4621-ba0e-a577c69283ef', 30, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', 'e2691144-3b1b-4841-9693-fad7af25bba9', 30, NOW());

insert into withdrawal (game_id, person_id, amount, created_at)
values  ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 5, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 15, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 15, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '72775968-3da6-469e-8a61-60104eacdb3a', 15, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '72775968-3da6-469e-8a61-60104eacdb3a', 10, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', 'e2691144-3b1b-4841-9693-fad7af25bba9', 15, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', 'e2691144-3b1b-4841-9693-fad7af25bba9', 15, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', 'e2691144-3b1b-4841-9693-fad7af25bba9', 15, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '58ae9984-1ebc-4621-ba0e-a577c69283ef', 30, NOW()),
        ('123934e5-2cf0-46c8-bc73-9a8ed6696e63', '58ae9984-1ebc-4621-ba0e-a577c69283ef', 15, NOW());


insert into game (id, stack, buy_in, bounty, game_type)
values  ('51d973b6-cde3-4bbb-b67b-7555243dbc15', 1000, 30, 0, 'CASH');

insert into chat_games (game_id, chat_id, created_at, message_id)
values  ('51d973b6-cde3-4bbb-b67b-7555243dbc15', 123, NOW(), 5);
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

-- withdrawals test
insert into game (id, stack, buy_in, bounty, game_type)
values  ('51d973b6-cde3-4bbb-b67b-7555243dbc15', 1000, 60, 0, 'CASH');

insert into chat_games (game_id, chat_id, created_at, message_id)
values  ('51d973b6-cde3-4bbb-b67b-7555243dbc15', 123, NOW(), 5);

insert into person (id, nick_name)
values  ('4ffeb45c-2d90-4c4f-abe7-1995b4be4810', 'mister'),
        ('e908c0ce-9766-424b-b3ae-5b817fcc1707', 'missis'),
        ('02ba745e-3afd-4a47-98d1-609c77d59216', 'smith'),
        ('d462bd1a-e5c3-47bd-958f-c7271fb2b773', 'candle');

insert into chat_persons (person_id, chat_id)
values  ('4ffeb45c-2d90-4c4f-abe7-1995b4be4810', 123),
        ('e908c0ce-9766-424b-b3ae-5b817fcc1707', 123),
        ('02ba745e-3afd-4a47-98d1-609c77d59216', 123),
        ('d462bd1a-e5c3-47bd-958f-c7271fb2b773', 123);

insert into entries (game_id, person_id, amount, created_at)
values  ('51d973b6-cde3-4bbb-b67b-7555243dbc15', '13b4108e-2dfa-4fea-8b7b-277e1c87d2d8', 60, NOW()), --kinger
        ('51d973b6-cde3-4bbb-b67b-7555243dbc15', '72775968-3da6-469e-8a61-60104eacdb3a', 60, NOW()), --queen
        ('51d973b6-cde3-4bbb-b67b-7555243dbc15', 'e2691144-3b1b-4841-9693-fad7af25bba9', 60, NOW()), --jackas
        ('51d973b6-cde3-4bbb-b67b-7555243dbc15', '58ae9984-1ebc-4621-ba0e-a577c69283ef', 60, NOW()), --tenten
        ('51d973b6-cde3-4bbb-b67b-7555243dbc15', '4ffeb45c-2d90-4c4f-abe7-1995b4be4810', 60, NOW()), --mister
        ('51d973b6-cde3-4bbb-b67b-7555243dbc15', 'e908c0ce-9766-424b-b3ae-5b817fcc1707', 60, NOW()), --missis
        ('51d973b6-cde3-4bbb-b67b-7555243dbc15', '02ba745e-3afd-4a47-98d1-609c77d59216', 60, NOW()), --smith
        ('51d973b6-cde3-4bbb-b67b-7555243dbc15', 'd462bd1a-e5c3-47bd-958f-c7271fb2b773', 60, NOW()); --candle

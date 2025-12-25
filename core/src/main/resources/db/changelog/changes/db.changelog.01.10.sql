--liquibase formatted sql

--changeset task#63:1
DROP FUNCTION check_game_updates;

--changeset task#63:2
with schemas as ( select game_id, schema from prize_pool where schema::text not like '%pos%' ),
  updated as (
    select game_id, jsonb_agg(jsonb_build_object('position', key::int, 'percentage', value)) as data from schemas
    cross join lateral jsonb_each(schema)
    group by game_id
    )
update prize_pool as pp
set schema = updated.data
from updated
where pp.game_id = updated.game_id;

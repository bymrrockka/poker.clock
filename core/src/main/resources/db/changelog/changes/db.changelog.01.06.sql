--liquibase formatted sql

--changeset task#47:1
-- update entries
with persons as (select (array_agg(id))[1] as id,
                        nick_name          as nick,
                        max(dub.count)
                 from person as p
                          join (select nick_name as nname, count(*) from person group by nick_name) as dub
                               on dub.nname = p.nick_name
                 where dub.count > 1
                 group by nick
                 order by max(dub.count) desc)
update entries
set person_id=id
from persons
where person_id in (select id from person where nick_name = persons.nick)
;

-- update withdrawal
with persons as (select (array_agg(id))[1] as id,
                        nick_name          as nick,
                        max(dub.count)
                 from person as p
                          join (select nick_name as nname, count(*) from person group by nick_name) as dub
                               on dub.nname = p.nick_name
                 where dub.count > 1
                 group by nick
                 order by max(dub.count) desc)
update withdrawal
set person_id=id
from persons
where person_id in (select id from person where nick_name = persons.nick)
;

-- update bounty
with persons as (select (array_agg(id))[1] as id,
                        nick_name          as nick,
                        max(dub.count)
                 from person as p
                          join (select nick_name as nname, count(*) from person group by nick_name) as dub
                               on dub.nname = p.nick_name
                 where dub.count > 1
                 group by nick
                 order by max(dub.count) desc)
update bounty
set from_person=id
from persons
where from_person in (select id from person where nick_name = persons.nick)
;


with persons as (select (array_agg(id))[1] as id,
                        nick_name          as nick,
                        max(dub.count)
                 from person as p
                          join (select nick_name as nname, count(*) from person group by nick_name) as dub
                               on dub.nname = p.nick_name
                 where dub.count > 1
                 group by nick
                 order by max(dub.count) desc)
update bounty
set to_person=id
from persons
where to_person in (select id from person where nick_name = persons.nick)
;


-- update finale_places
with persons as (select (array_agg(id))[1] as id,
                        nick_name          as nick,
                        max(dub.count)
                 from person as p
                          join (select nick_name as nname, count(*) from person group by nick_name) as dub
                               on dub.nname = p.nick_name
                 where dub.count > 1
                 group by nick
                 order by max(dub.count) desc)
update finale_places
set person_id=id
from persons
where person_id in (select id from person where nick_name = persons.nick)
;

-- update money_transfer
with persons as (select (array_agg(id))[1] as id,
                        nick_name          as nick,
                        max(dub.count)
                 from person as p
                          join (select nick_name as nname, count(*) from person group by nick_name) as dub
                               on dub.nname = p.nick_name
                 where dub.count > 1
                 group by nick
                 order by max(dub.count) desc)
update money_transfer
set person_id=id
from persons
where person_id in (select id from person where nick_name = persons.nick)
;

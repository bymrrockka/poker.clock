--liquibase formatted sql

--changeset task#47:3
-- remove duplicates in person table
with persons as (select (array_agg(id))[1] as id,
                        nick_name          as nick,
                        max(dub.count)
                 from person as p
                          join (select nick_name as nname, count(*) from person group by nick_name) as dub
                               on dub.nname = p.nick_name
                 where dub.count > 1
                 group by nick
                 order by max(dub.count) desc)
delete
from person
where id in (select id from person where nick_name in (select distinct nick from persons))
  and id not in (select id from persons)
;

--changeset task#47:4
ALTER TABLE person
    ADD CONSTRAINT person_nick_name_key UNIQUE (nick_name);
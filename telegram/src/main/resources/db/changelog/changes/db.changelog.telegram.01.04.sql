--liquibase formatted sql

--changeset task#47:2
with persons as (
        select (array_agg(id))[1] as id,
                nick_name as nick,
                max(dub.count)
            from person as p
        join (select nick_name as nname, count(*) from person group by nick_name) as dub on dub.nname = p.nick_name
        where dub.count > 1
        group by nick
        order by max(dub.count) desc)
delete from chat_persons
    where person_id in (select id from person where nick_name in (select distinct nick from persons))
        and person_id not in (select id from persons)
;

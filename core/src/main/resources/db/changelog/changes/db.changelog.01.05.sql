--liquibase formatted sql

--changeset task#36:2

CREATE OR REPLACE FUNCTION check_game_updates(gid uuid, finished_at timestamp = NULL)
RETURNS BOOLEAN AS $$
DECLARE updates integer;
BEGIN
        if (finished_at is null) then RETURN true; end if;

        select count(created_at) INTO updates
        from entries
        where game_id = gid and created_at > finished_at
        group by game_id;

        if (updates > 0) then RETURN true; end if;

        select count(created_at) INTO updates
        from withdrawal
        where game_id = gid and created_at > finished_at
        group by game_id;

        if (updates > 0) then RETURN true; end if;

        select count(created_at) INTO updates
        from bounty
        where game_id = gid and created_at > finished_at
        group by game_id;

        if (updates > 0) then RETURN true;
        else RETURN false;
        end if;
END;
$$  LANGUAGE plpgsql;

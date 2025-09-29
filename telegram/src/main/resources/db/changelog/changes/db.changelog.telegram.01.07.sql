--liquibase formatted sql

--changeset task#45:3
--update json data in db to fit with new models
UPDATE poll_task
SET options = jsonb_set(options #- '{0,isParticipant}', '{0,participant}', (options #> '{0,isParticipant}'))
WHERE (options #> '{0,isParticipant}') IS NOT NULL;

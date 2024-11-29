create table IF NOT EXISTS shedule
(
    id       bigserial primary key,
    guide_id integer not null,
    day      date    null,
    start    time    null,
    finish   time    null
)

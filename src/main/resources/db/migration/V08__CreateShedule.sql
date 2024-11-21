create table IF NOT EXISTS shedule
(
    id        bigserial primary key,
    guide_id   integer     not null,
    start      timestamp   null,
    finish     timestamp   null
)

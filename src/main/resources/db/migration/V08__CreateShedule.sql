create table IF NOT EXISTS shedule
(
    id        bigserial primary key,
    trip_id   integer not null,
    begin     time   null
)

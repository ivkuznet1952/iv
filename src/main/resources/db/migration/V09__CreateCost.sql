create table IF NOT EXISTS cost
(
    id             bigserial primary key,
    trip_id        integer ,
    transport_id   integer,
    cost           integer
)

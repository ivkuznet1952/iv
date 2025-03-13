create table IF NOT EXISTS photos
(
    id             bigserial primary key,
    trip_id        integer ,
    bytes          bytea,
    name           varchar(200)
)
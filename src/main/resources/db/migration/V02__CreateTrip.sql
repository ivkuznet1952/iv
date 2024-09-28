create table IF NOT EXISTS trip
(
    id   bigserial primary key,
    name varchar(100) not null,
    description varchar(500) not null,
    photo varchar(100) not null,
    comment varchar(100) not null,
    duration integer,
    active boolean
)

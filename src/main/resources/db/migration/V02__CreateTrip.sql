create table IF NOT EXISTS trip
(
    id   bigserial primary key,
    name varchar(100) not null,
    description varchar(500) null,
    photo varchar(100) null,
    comment varchar(100) null,
    duration numeric,
    active boolean
)

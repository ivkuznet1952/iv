create table IF NOT EXISTS transport
(
    id       bigserial primary key,
    active   boolean      not null,
    maxcount integer,
    name     varchar(100) not null
)

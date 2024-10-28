create table IF NOT EXISTS transport
(
    id   bigserial primary key,
    active    boolean      not null,
    name varchar(100) not null
)

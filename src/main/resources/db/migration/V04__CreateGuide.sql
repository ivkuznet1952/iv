create table IF NOT EXISTS guide
(
    id bigserial primary key,
    active    boolean      not null,
    comment   varchar(500) null,
    email     varchar(255) null,
    firstname varchar(255) null,
    lastname  varchar(255) null,
    phone     varchar(255) null
)


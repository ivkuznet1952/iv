create table IF NOT EXISTS customer
(
    id             bigserial primary key,
    comment        varchar(500) null,
    email          varchar(255) null,
    hashedPassword varchar(255) null,
    firstname      varchar(255) not null,
    lastname       varchar(255) not null,
    phone          varchar(255) null,
    created        timestamp    null,
    updated        timestamp    null
)

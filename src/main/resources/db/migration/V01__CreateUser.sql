
create table IF NOT EXISTS users
(
    id bigserial primary key not null,
    username varchar(100) not null,
    hashedPassword varchar(200) not null,
    role varchar(100) not null,
    created timestamp,
    updated timestamp,
    active boolean
);
create unique index on users(username);


create table IF NOT EXISTS users
(
    id bigserial primary key not null,
    username varchar(100) not null,
    hashedPassword varchar(200) not null,
    roles varchar(400) not null,
    created timestamp,
    updated timestamp
);
create unique index on users(username);

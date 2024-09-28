create table IF NOT EXISTS log
(
    id      bigserial primary key,
    created timestamp,
    user_id integer      not null,
    action  varchar(100) not null,
    comment varchar(100) not null
)

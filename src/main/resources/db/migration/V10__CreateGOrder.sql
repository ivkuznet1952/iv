create table IF NOT EXISTS gorder
(
    id           bigserial primary key,
    num          integer   not null,
    trip_id      integer   not null,
    start        time      null,
    day          date,
    transport_id integer   not null,
    guide_id     integer   not null,
    count        integer,
    cost         integer,
    status       varchar(100),
    paystatus    varchar(100),
    customer_id  integer,
    is_online    boolean,
    comment      text,
    created      timestamp null,
    updated      timestamp null,
    createdby_id integer,
    archived     boolean
)



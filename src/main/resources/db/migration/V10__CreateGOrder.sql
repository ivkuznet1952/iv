create table IF NOT EXISTS gorder
(
    id           bigserial primary key,
    num          integer   not null,
    trip_id      integer   not null,
    start        timestamp null,
    transport_id integer   not null,
    guide_id     integer   not null,
    count        integer,
    cost         integer,
    status       varchar(100),
    paystatus    varchar(100),
    customer_id  integer,
    comment      text,
    created      timestamp null,
    updated      timestamp null,
    createdby    integer,
    archived     boolean
)



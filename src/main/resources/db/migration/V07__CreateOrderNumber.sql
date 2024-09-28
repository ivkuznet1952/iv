create table IF NOT EXISTS ordernumber
(
    id     bigserial primary key,
    num    integer not null,
    org_id integer not null
)

create table if not exists author
(
    id         bigint primary key generated always as identity,
    first_name varchar(64) not null,
    last_name  varchar(64) not null,
    bio        varchar(4096),
    birth_date date,
    created_at timestamp,
    updated_at timestamp,
    version    int default 0
);
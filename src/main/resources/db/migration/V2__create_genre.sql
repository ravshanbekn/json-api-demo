create table if not exists genre
(
    id          bigint primary key generated always as identity,
    name        varchar(64) not null,
    description varchar(4096),
    created_at  timestamp,
    updated_at  timestamp,
    version     int default 0
);
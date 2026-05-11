create table if not exists book (
    id bigint primary key generated always as identity,
    title varchar(128) not null,
    summary varchar(4096),
    published_year int,
    isbn varchar(64),
    author_id bigint references author(id),
    created_at  timestamp,
    updated_at  timestamp,
    version     int default 0
);
create table if not exists book_genre (
    book_id bigint references book(id) not null,
    genre_id bigint references genre(id) not null,
    primary key (book_id, genre_id)
);
insert into author (first_name, last_name, bio, birth_date) values
    ('J.R.R.', 'Tolkien', 'English author and philologist, best known for The Lord of the Rings.', '1892-01-03'),
    ('Frank', 'Herbert', 'American science fiction author, best known for the Dune series.', '1920-10-08'),
    ('George', 'Orwell', 'English novelist and essayist, known for Nineteen Eighty-Four and Animal Farm.', '1903-06-25');

insert into genre (name, description) values
    ('Fantasy', 'Fiction involving magic and supernatural elements.'),
    ('Science Fiction', 'Fiction based on imagined future scientific discoveries.'),
    ('Dystopian', 'Fiction set in a repressive and controlled society.'),
    ('Adventure', 'Fiction involving exciting journeys and events.');

insert into book (title, summary, published_year, isbn, author_id) values
    ('The Hobbit', 'A hobbit goes on an unexpected adventure with a group of dwarves.', 1937, '978-0-547-92822-7', 1),
    ('The Lord of the Rings', 'The epic quest to destroy the One Ring.', 1954, '978-0-618-64015-7', 1),
    ('Dune', 'A young nobleman navigates politics and survival on a desert planet.', 1965, '978-0-441-17271-9', 2),
    ('Dune Messiah', 'The sequel to Dune, exploring the consequences of power.', 1969, '978-0-441-17269-6', 2),
    ('Nineteen Eighty-Four', 'A dystopian novel about totalitarian surveillance society.', 1949, '978-0-451-52493-5', 3);

insert into book_genre (book_id, genre_id) values
    (1, 1), (1, 4),
    (2, 1), (2, 4),
    (3, 2), (3, 4),
    (4, 2),
    (5, 3);

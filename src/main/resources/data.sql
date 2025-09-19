INSERT INTO mpa(mpa_name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO genres(genre_name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO PUBLIC.films(id,name,description,release_date,duration,mpa_id) VALUES
                                                                               (5L, 'Фильм5', 'Описание фильма5', '1985-7-14', 145, 1),
                                                                               (6L, 'Фильм6', 'Описание фильма6', '1985-9-24', 125, 2),
                                                                               (7L, 'Фильм7', 'Описание фильма7', '1981-2-15', 127, 3),
                                                                               (8L, 'Фильм8', 'Описание фильма8', '1985-2-15', 127, 3);

INSERT INTO PUBLIC.users(id,email,login,birthday,name) VALUES
                                                           (11L, 'iv87mail@mail.ru', '14login', '1985-7-14', 'Алекс'),
                                                           (12L, 'andy85mail@mail.ru', '16login', '1982-7-14',  'Марк'),
                                                           (13L, 'al86mail@mail.ru', '17login', '1989-7-14', 'Валерия'),
                                                           (14L, 'kir85mail@mail.ru', '18login', '1995-7-14', 'Константин');

INSERT INTO PUBLIC.likes(film_id,user_id) VALUES
                                              (5L, 11L),
                                              (5L, 12L),
                                              (5L, 13L),
                                              (6L, 11L),
                                              (6L, 12L),
                                              (6L, 13L),
                                              (5L, 14L),
                                              (7L, 13L);


INSERT INTO PUBLIC.film_genres(genre_id, film_id) VALUES
                                                      (1, 5L),
                                                      (1, 6L),
                                                      (2, 6L),
                                                      (2, 7L),
                                                      (1, 8L);


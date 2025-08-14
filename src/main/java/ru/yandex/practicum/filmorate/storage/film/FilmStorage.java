package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Long id);

    Collection<Film> getAllFilms();

    Film getById(Long id);
}

package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Long id);

    Collection<Film> getAllFilms();

    Optional<Film> getById(Long id);

    Collection<Film> getFilmsByDirector(Long directorId, String sortBy);
}

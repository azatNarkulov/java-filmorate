package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Long id);

    Collection<Film> getAllFilms();

    Optional<Film> getById(Long id);

    Collection<Film> getFilmsByDirector(Long directorId, String sortBy);

    List<Film> getTopFilmsByGenreAndYear(int limit, Integer genreId, String year);

    List<Film> getTopFilmsByGenre(int limit, Integer genreId);

    List<Film> getTopFilms(int limit);

    List<Film> getTopFilmsByYear(int limit, String year);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getFilmsByDirectorOrTitleSortByLike(String query, String director, String title);
}

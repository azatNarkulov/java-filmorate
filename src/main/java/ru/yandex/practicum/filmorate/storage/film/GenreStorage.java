package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> getAllGenres();

    Optional<Genre> getById(int id);

    List<Genre> getGenresByFilmId(Long filmId);
}

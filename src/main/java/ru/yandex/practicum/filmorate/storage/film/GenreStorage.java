package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> getAllGenres();

    Optional<Genre> getById(int id);

    Set<Genre> getGenresByFilmId(Long filmId);
}

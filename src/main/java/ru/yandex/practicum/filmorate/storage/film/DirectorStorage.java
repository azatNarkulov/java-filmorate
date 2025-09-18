package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> getAllDirectors();

    Optional<Director> getById(Long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long id);
}

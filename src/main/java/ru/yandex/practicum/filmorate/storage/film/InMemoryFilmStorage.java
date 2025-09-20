package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Optional<Film> getById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    private long generateId() {
        return id++;
    }

    @Override
    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        throw new UnsupportedOperationException("Метод не реализован для in-memory хранилища");
    }

    @Override
    public List<Film> getTopFilmsByGenreAndYear(int limit, Integer genreId, String year) {
        throw new UnsupportedOperationException("Метод не реализован для in-memory хранилища");
    }

    @Override
    public List<Film> getTopFilmsByGenre(int limit, Integer genreId) {
        throw new UnsupportedOperationException("Метод не реализован для in-memory хранилища");
    }

    @Override
    public List<Film> getTopFilms(int limit) {
        throw new UnsupportedOperationException("Метод не реализован для in-memory хранилища");
    }

    @Override
    public List<Film> getTopFilmsByYear(int limit, String year) {
        throw new UnsupportedOperationException("Метод не реализован для in-memory хранилища");
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        throw new UnsupportedOperationException("Метод не реализован для in-memory хранилища");
    }

    @Override
    public List<Film> getFilmsByDirectorOrTitleSortByLike(String query, String director, String title) {
        throw new UnsupportedOperationException("Метод не реализован для in-memory хранилища");
    }
}

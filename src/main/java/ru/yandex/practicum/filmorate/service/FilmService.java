package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film deleteFilm(Long id) {
        return filmStorage.deleteFilm(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        film.getLikesId().add(userId);
        log.debug("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        film.getLikesId().remove(userId);
        log.debug("Пользователь {} убрал лайк к фильму {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikesId().size()).reversed())
                .limit(count)
                .toList();
    }
}

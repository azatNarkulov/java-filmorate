package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film getFilmById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public Film createFilm(Film film) {
        log.debug("Добавляем фильм: {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        getFilmById(newFilm.getId());
        log.debug("Обновляем данные фильма: {}", newFilm);
        return filmStorage.updateFilm(newFilm);
    }

    public Film deleteFilm(Long id) {
        getFilmById(id);
        log.debug("Удаляем фильм: {}", filmStorage.getById(id));
        return filmStorage.deleteFilm(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        checkUserExist(userId);

        film.getLikesId().add(userId);
        log.debug("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        checkUserExist(userId);

        film.getLikesId().remove(userId);
        log.debug("Пользователь {} убрал лайк к фильму {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikesId().size()).reversed())
                .limit(count)
                .toList();
    }

    private void checkUserExist(Long userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}

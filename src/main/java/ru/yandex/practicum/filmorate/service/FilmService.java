package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.Event;
import ru.yandex.practicum.filmorate.model.user.EventType;
import ru.yandex.practicum.filmorate.model.user.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikeStorage likeStorage;
    private final EventService eventService;
    private final DirectorService directorService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreStorage genreStorage,
                       MpaStorage mpaStorage,
                       LikeStorage likeStorage,
                       EventService eventService,
                       DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.likeStorage = likeStorage;
        this.eventService = eventService;
        this.directorService = directorService;
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public Film createFilm(Film film) {
        log.debug("Добавляем фильм: {}", film);

        if (film.getMpa() == null || film.getMpa().getId() == 0) {
            throw new NotFoundException("Mpa не указан");
        }

        int mpaId = film.getMpa().getId();
        Mpa mpa = mpaStorage.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Mpa с id: " + mpaId + " не найден"));
        film.setMpa(mpa);

        for (Genre genre : film.getGenres()) {
            genreStorage.getById(genre.getId())
                    .orElseThrow(() -> new NotFoundException("Жанр с id " + genre.getId() + " не найден"));
        }

        log.info("Проверка прошла, вставляем фильм с mpa_id = {}", film.getMpa().getId());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        getFilmById(newFilm.getId());
        log.debug("Обновляем данные фильма: {}", newFilm);
        if (newFilm.getDirectors() != null) {
            newFilm.getDirectors().forEach(director ->
                    directorService.getById(director.getId())
            );
        }
        return filmStorage.updateFilm(newFilm);
    }

    public List<Film> getTopFilmsByGenreOrYear(int limit, Integer genreId, String year) {
        boolean hasYear = year != null;
        boolean hasGenre = genreId != null && genreId > 0;

        if (hasYear && hasGenre) {
            return filmStorage.getTopFilmsByGenreAndYear(limit, genreId, year);
        }
        if (hasYear) {
            return filmStorage.getTopFilmsByYear(limit, year);
        }
        if (hasGenre) {
            return filmStorage.getTopFilmsByGenre(limit, genreId);
        }
        return filmStorage.getTopFilms(limit);
    }

    public void deleteFilm(Long id) {
        Film film = getFilmById(id);
        log.debug("Удаляем фильм: {}", film);
        filmStorage.deleteFilm(id);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long userId) {
        if (filmStorage.getById(filmId).isEmpty() || userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Не найдено");
        }
        likeStorage.addLike(filmId, userId);
        log.debug("Пользователь {} поставил лайк фильму {}", userId, filmId);
        eventService.createEvent(new Event(userId, EventType.LIKE, Operation.ADD, filmId));
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.getById(filmId).isEmpty() || userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Не найдено");
        }
        likeStorage.removeLike(filmId, userId);
        log.debug("Пользователь {} убрал лайк к фильму {}", userId, filmId);
        eventService.createEvent(new Event(userId, EventType.LIKE, Operation.REMOVE, filmId));
    }

    public List<Film> getFilmsByDirectorOrTitleSortByLike(String query, String by) {

        AtomicReference<String> director = new AtomicReference<>("");
        AtomicReference<String> title = new AtomicReference<>("");
        Arrays.stream(by.split(",")).forEach(q -> {
            if (q.trim().equals("director")) director.set("director");
            if (q.trim().equals("title")) title.set("title");
        });
        return filmStorage.getFilmsByDirectorOrTitleSortByLike("%" + query.toLowerCase() + "%", director.get(), title.get());
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .peek(film -> film.setLikesId(new HashSet<>(likeStorage.getLikesByFilmId(film.getId()))))
                .sorted(Comparator.comparingInt((Film film) -> film.getLikesId().size()).reversed())
                .limit(count)
                .toList();
    }

    public List<Genre> findAllGenres() {
        return new ArrayList<>(genreStorage.getAllGenres());
    }

    public Genre findGenreById(int id) {
        return genreStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }

    public List<Mpa> findAllMpa() {
        return new ArrayList<>(mpaStorage.getAllMpa());
    }

    public Mpa findMpaById(int id) {
        return mpaStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }

    private void checkUserExist(Long userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        directorService.getById(directorId);
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }
}

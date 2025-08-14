package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final UserStorage userStorage = new InMemoryUserStorage();
    private FilmService filmService;
    private Film film;

    @BeforeEach
    public void setUp() {
        filmService = new FilmService(filmStorage, userStorage);
        film = createValidFilm();
    }

    @Test
    public void createFilm_addFilm_filmIsValid() {
        Film addedFilm = filmService.createFilm(film);

        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
    }

    @Test
    public void updateFilm_updateFilm_filmIsValid() {
        filmService.createFilm(film);
        film.setName("Побег из курятника");
        Film updatedFilm = filmService.updateFilm(film);

        assertEquals(film.getName(), updatedFilm.getName());
    }

    @Test
    public void deleteFilm_deleteFilm() {
        filmService.createFilm(film);
        filmService.deleteFilm(film.getId());
        assertEquals(filmService.getAllFilms().size(), 0);
    }

    @Test
    public void getAllFilms_getFilms() {
        filmService.createFilm(film);

        Film anotherFilm = new Film();
        anotherFilm.setName("Побег из курятника");
        anotherFilm.setDescription("Пластилиновый мультфильм");
        anotherFilm.setReleaseDate(LocalDate.of(2000, 6, 21));
        anotherFilm.setDuration(104);
        filmService.createFilm(anotherFilm);

        assertEquals(2, filmService.getAllFilms().size());
    }

    @Test
    public void getFilmById_getFilmById() {
        filmService.createFilm(film);
        Film receivedFilm = filmService.getFilmById(film.getId());
        assertEquals(film.getId(), receivedFilm.getId());
    }

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Побег из Шоушенка");
        film.setDescription("Фильм по рассказу Стивена Кинга");
        film.setReleaseDate(LocalDate.of(1994, 10, 14));
        film.setDuration(144);

        return film;
    }
}

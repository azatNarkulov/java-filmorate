package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
        film = createValidFilm();
    }

    @Test
    public void addFilm_addFilm_filmIsValid() {
        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm);
        assertEquals(1, addedFilm.getId());
    }

    @Test
    public void updateFilm_updateFilm_filmIsValid() {
        filmController.addFilm(film);
        film.setName("Побег из курятника");
        Film updatedFilm = filmController.updateFilm(film);

        assertEquals(film.getName(), updatedFilm.getName());
    }

    @Test
    public void getFilms_getFilms() {
        filmController.addFilm(film);

        Film anotherFilm = new Film();
        anotherFilm.setName("Побег из курятника");
        anotherFilm.setDescription("Пластилиновый мультфильм");
        anotherFilm.setReleaseDate(LocalDate.of(2000, 6, 21));
        anotherFilm.setDuration(104);
        filmController.addFilm(anotherFilm);

        assertEquals(2, filmController.getFilms().size());
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

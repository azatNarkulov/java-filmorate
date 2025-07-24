package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    /*Не мог понять, как в юнит-тестах проверять валидацию, если я использовал аннотации типа @NotNull, @NotBlank и т.д.
    При этом в Postman у меня все тесты прошли – т.е. валидация работает
    Поэтому я решил протестировать здесь только валидацию вне аннотаций*/

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
    public void addFilm_throwException_filmReleasedBeforeMinDate() {
        film.setReleaseDate(LocalDate.of(1825, 12, 25));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film), "Дата релиза не может быть раньше 28 декабря 1895 года");
    }

    @Test
    public void updateFilm_updateFilm_filmIsValid() {
        filmController.addFilm(film);
        film.setName("Побег из курятника");
        Film updatedFilm = filmController.updateFilm(film);

        assertEquals(film.getName(), updatedFilm.getName());
    }

    @Test
    public void updateFilm_throwException_filmReleasedBeforeMinDate() {
        filmController.addFilm(film);
        film.setReleaseDate(LocalDate.of(1825, 12, 25));

        assertThrows(ValidationException.class, () -> filmController.updateFilm(film), "Дата релиза не может быть раньше 28 декабря 1895 года");
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

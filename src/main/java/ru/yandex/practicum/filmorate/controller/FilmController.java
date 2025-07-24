package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Добавляем фильм: {}", film);

        validateFilm(film);

        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм " + newFilm.getName() + " не найден");
        }
        log.debug("Обновляем данные фильма: {}", newFilm);

        validateFilm(newFilm);

        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private long generateId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

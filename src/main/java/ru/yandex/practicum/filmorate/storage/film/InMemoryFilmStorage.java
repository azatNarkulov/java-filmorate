package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public Film addFilm(Film film) {
        log.debug("Добавляем фильм: {}", film);

        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }
        log.debug("Обновляем данные фильма: {}", newFilm);

        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film deleteFilm(Long id) {
        Film film = films.remove(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        log.debug("Удаляем фильм: {}", film);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    private long generateId() {
        return id++;
    }
}

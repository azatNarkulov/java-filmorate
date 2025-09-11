package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> mapper = (rs, rowNum) -> {
        int id = rs.getInt("id");
        String name = rs.getString("genre_name");

        return new Genre(id, name);
    };

    @Override
    public Collection<Genre> getAllGenres() {
        String findAllQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(findAllQuery, mapper);
    }

    @Override
    public Optional<Genre> getById(int id) {
        String findByIdQuery = "SELECT * FROM genres WHERE id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(findByIdQuery, mapper, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        String findByFilmIdQuery = "SELECT * " +
                "FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE film_id = ? " +
                "ORDER BY g.id";
        return jdbcTemplate.query(findByFilmIdQuery, mapper, filmId);
    }
}

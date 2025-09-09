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

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_BY_FILMID_QUERY = "SELECT * " +
            "FROM genres g " +
            "JOIN film_genres fg ON g.id = fg.genre_id " +
            "WHERE film_id = ? " +
            "ORDER BY g.id";

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Optional<Genre> getById(int id) {
        try {
            Genre genre = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        return jdbcTemplate.query(FIND_BY_FILMID_QUERY, mapper, filmId);
    }
}

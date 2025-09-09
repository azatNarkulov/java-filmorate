package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    private static final String FIND_ALL_QUERY = "SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, m.id AS mpa_id, m.mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, m.id AS mpa_id, m.mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.id " +
            "WHERE f.id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films " +
            "WHERE id = ?";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres " +
            "WHERE film_id = ?";
    private static final String INSERT_GENRES_QUERY = "INSERT INTO film_genres(film_id, genre_id) " +
            "VALUES (?, ?)";


    @Override
    public Film addFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            film.setId(id);
        }
        saveFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int rows = jdbcTemplate.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        System.out.println("Обновлено строк: " + rows);

        jdbcTemplate.update(DELETE_GENRES_QUERY, film.getId());
        saveFilmGenres(film);

        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        jdbcTemplate.update(DELETE_GENRES_QUERY, id);
        jdbcTemplate.update(DELETE_QUERY, id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query(FIND_ALL_QUERY, mapper());
    }

    @Override
    public Optional<Film> getById(Long id) {
        try {
            Film film = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, mapper(), id);
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;

        Set<Genre> uniqueGenres = film.getGenres().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparingInt(Genre::getId))
                ));

        for (Genre genre : uniqueGenres) {
            jdbcTemplate.update(INSERT_GENRES_QUERY, film.getId(), genre.getId());
        }
    }

    private RowMapper<Film> mapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            int mpaId = rs.getInt("mpa_id");
            String mpaName = rs.getString("mpa_name");
            film.setMpa(new Mpa(mpaId, mpaName));

            film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
            return film;
        };
    }
}

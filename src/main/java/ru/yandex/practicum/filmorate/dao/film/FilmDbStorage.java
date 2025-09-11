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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String insertQuery = "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
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
        String updateQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE id = ?";
        String deleteGenresQuery = "DELETE FROM film_genres " +
                "WHERE film_id = ?";
        int rows = jdbcTemplate.update(updateQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        System.out.println("Обновлено строк: " + rows);

        jdbcTemplate.update(deleteGenresQuery, film.getId());
        saveFilmGenres(film);

        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        String deleteGenresQuery = "DELETE FROM film_genres " +
                "WHERE film_id = ?";
        String deleteQuery = "DELETE FROM films " +
                "WHERE id = ?";
        jdbcTemplate.update(deleteGenresQuery, id);
        jdbcTemplate.update(deleteQuery, id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        String findAllQuery = "SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, m.id AS mpa_id, m.mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.id";

        List<Film> films = jdbcTemplate.query(findAllQuery, mapper());
        if (films.isEmpty()) return films;

        Map<Long, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }

        List<Long> filmsIds = new ArrayList<>(filmMap.keySet());

        String findGenresForFilmQuery = "SELECT fg.film_id, g.id AS genre_id, g.genre_name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (" + String.join(",", Collections.nCopies(filmsIds.size(), "?")) + ")";

        jdbcTemplate.query(findGenresForFilmQuery, filmsIds.toArray(), (rs) -> {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);

            if (film != null) {
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
        });
        return films;
    }

    @Override
    public Optional<Film> getById(Long id) {
        String findByIdQuery = "SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, m.id AS mpa_id, m.mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(findByIdQuery, mapper(), id);
            if (film != null) {
                String findGenresForFilmQuery = "SELECT g.id AS genre_id, g.genre_name " +
                        "FROM film_genres fg " +
                        "JOIN genres g ON fg.genre_id = g.id " +
                        "WHERE fg.film_id = ? " +
                        "ORDER BY g.id";
                List<Genre> genres = jdbcTemplate.query(findGenresForFilmQuery, (rs, rowNum) ->
                        new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), id);

                film.getGenres().clear();
                film.getGenres().addAll(genres);
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private void saveFilmGenres(Film film) {
        String insertGenresQuery = "INSERT INTO film_genres(film_id, genre_id) " +
                "VALUES (?, ?)";
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;

        Set<Genre> uniqueGenres = film.getGenres().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparingInt(Genre::getId))
                ));

        for (Genre genre : uniqueGenres) {
            jdbcTemplate.update(insertGenresQuery, film.getId(), genre.getId());
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

            return film;
        };
    }
}

package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String deleteDirectorsQuery = "DELETE FROM film_directors WHERE film_id = ?";

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
        saveFilmDirectors(film);
        return film;
    }

    @Transactional
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
        jdbcTemplate.update(deleteDirectorsQuery, film.getId());
        saveFilmGenres(film);
        saveFilmDirectors(film);

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

        String findDirectorsForFilmQuery = "SELECT fd.film_id, d.id AS director_id, d.name AS director_name " +
                "FROM film_directors fd " +
                "JOIN directors d ON fd.director_id = d.id " +
                "WHERE fd.film_id IN (" + String.join(",", Collections.nCopies(filmsIds.size(), "?")) + ")";

        jdbcTemplate.query(findDirectorsForFilmQuery, filmsIds.toArray(), (rs) -> {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);

            if (film != null) {
                Director director = new Director();
                director.setId(rs.getLong("director_id"));
                director.setName(rs.getString("director_name"));
                film.getDirectors().add(director);
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

                String findDirectorsForFilmQuery = "SELECT d.id AS director_id, d.name AS director_name " +
                        "FROM film_directors fd " +
                        "JOIN directors d ON fd.director_id = d.id " +
                        "WHERE fd.film_id = ?";
                List<Director> directors = jdbcTemplate.query(findDirectorsForFilmQuery, (rs, rowNum) -> {
                    Director director = new Director();
                    director.setId(rs.getLong("director_id"));
                    director.setName(rs.getString("director_name"));
                    return director;
                }, id);

                film.getDirectors().clear();
                film.getDirectors().addAll(directors);
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

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql;
        sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "r.mpa_name, count(fl.user_id) AS flikes FROM films f " +
                "JOIN mpa r ON f.mpa_id = r.id " +
                "LEFT JOIN likes fl ON f.id = fl.film_id " +
                "WHERE fl.user_id = ? AND f.id IN (SELECT l.film_id " +
                "FROM likes l WHERE l.user_id = ? )" +
                "GROUP BY f.id " +
                "ORDER BY flikes desc";

        List<Film> films = jdbcTemplate.query(sql, mapper(), userId, friendId);
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

    public List<Film> getTopFilmsByGenreOrYear(int limit, Integer genreId, String year) {
        if ((genreId != null) && !year.isEmpty()) {
            return getTopFilmsByGenreAndYear(limit, genreId, year);
        }
        if ((genreId == null) && !year.isEmpty()) {
            return  getTopFilmsByYear(limit, year);
        }
        if ((genreId != null) && year.isEmpty()) {
            return  getTopFilmsByGenre(limit, genreId);
        }
        return getTopFilms(limit);
    }

    public List<Film> getTopFilmsByGenreAndYear(int limit, Integer genreId, String year) {
        String sql;
        sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id," +
                "r.mpa_name, count(fl.user_id) AS flikes FROM films f \n" +
                "JOIN mpa r ON f.mpa_id = r.id \n" +
                "LEFT JOIN likes fl ON f.id = fl.film_id \n" +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id \n" +
                "WHERE YEAR(f.release_date) = ? AND fg.genre_id = ?" +
                "GROUP BY f.id \n" +
                "ORDER BY flikes desc limit ?";
        List<Film> films = jdbcTemplate.query(sql, mapper(), year, genreId, limit);
        loadGenresAndDirectorsForFilms(films);
        return films;
    }

    public List<Film> getTopFilmsByGenre(int limit, Integer genreId) {
        String sql;
        sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id," +
                "r.mpa_name, count(fl.user_id) AS flikes FROM films f \n" +
                "JOIN mpa r ON f.mpa_id = r.id \n" +
                "LEFT JOIN likes fl ON f.id = fl.film_id \n" +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id \n" +
                "WHERE fg.genre_id = ?" +
                "GROUP BY f.id \n" +
                "ORDER BY flikes desc limit ?";
        List<Film> films =  jdbcTemplate.query(sql, mapper(), genreId, limit);
        loadGenresAndDirectorsForFilms(films);
        return films;
    }

    public List<Film> getTopFilms(int limit) {
        String sql;
        sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id," +
                "r.mpa_name, count(fl.user_id) AS flikes FROM films f \n" +
                "JOIN mpa r ON f.mpa_id = r.id \n" +
                "LEFT JOIN likes fl ON f.id = fl.film_id \n" +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id \n" +
                "GROUP BY f.id \n" +
                "ORDER BY flikes desc limit ?";
        List<Film> films =  jdbcTemplate.query(sql, mapper(), limit);
        loadGenresAndDirectorsForFilms(films);
        return films;
    }

    public List<Film> getTopFilmsByYear(int limit, String year) {
        String sql;
        sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id," +
                "r.mpa_name, count(fl.user_id) AS flikes FROM films f \n" +
                "JOIN mpa r ON f.mpa_id = r.id \n" +
                "LEFT JOIN likes fl ON f.id = fl.film_id \n" +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id \n" +
                "WHERE YEAR(f.release_date) = ? " +
                "GROUP BY f.id \n" +
                "ORDER BY flikes desc limit ?";
        List<Film> films =  jdbcTemplate.query(sql, mapper(), year, limit);
        loadGenresAndDirectorsForFilms(films);
        return films;
    }

    private RowMapper<Film> mapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
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

    private void saveFilmDirectors(Film film) {
        String insertDirectorsQuery = "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(insertDirectorsQuery, film.getId(), director.getId());
            }
        }
    }

    private void loadFilmDirectors(Film film) {
        String sql = "SELECT d.* FROM directors d " +
                "JOIN film_directors fd ON d.id = fd.director_id " +
                "WHERE fd.film_id = ?";

        Set<Director> directors = new HashSet<>(
                jdbcTemplate.query(sql, (rs, rowNum) -> {
                    Director director = new Director();
                    director.setId(rs.getLong("id"));
                    director.setName(rs.getString("name"));
                    return director;
                }, film.getId())
        );

        film.setDirectors(directors);
    }

    @Override
    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        String sql = "SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.id " +
                "JOIN film_directors fd ON f.id = fd.film_id " +
                "WHERE fd.director_id = ?";

        if ("year".equals(sortBy)) {
            sql += " ORDER BY f.release_date";
        } else if ("likes".equals(sortBy)) {
            sql = "SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, " +
                    "m.id AS mpa_id, m.mpa_name, COUNT(l.user_id) as likes_count " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.id " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN likes l ON f.id = l.film_id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, m.id, m.mpa_name " +
                    "ORDER BY likes_count DESC";
        }

        List<Film> films = jdbcTemplate.query(sql, mapper(), directorId);
        loadGenresAndDirectorsForFilms(films);
        return films;
    }

    private void loadGenresAndDirectorsForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        List<Long> filmIds = new ArrayList<>(filmMap.keySet());

        String genresPlaceholders = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String genresSql = "SELECT fg.film_id, g.id, g.genre_name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (" + genresPlaceholders + ")";
        jdbcTemplate.query(genresSql, filmIds.toArray(), rs -> {
            Film film = filmMap.get(rs.getLong("film_id"));
            if (film != null) {
                film.getGenres().add(new Genre(rs.getInt("id"), rs.getString("genre_name")));
            }
        });

        String directorsPlaceholders = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String directorsSql = "SELECT fd.film_id, d.id, d.name FROM film_directors fd " +
                "JOIN directors d ON fd.director_id = d.id " +
                "WHERE fd.film_id IN (" + directorsPlaceholders + ")";
        jdbcTemplate.query(directorsSql, filmIds.toArray(), rs -> {
            Film film = filmMap.get(rs.getLong("film_id"));
            if (film != null) {
                Director director = new Director();
                director.setId(rs.getLong("id"));
                director.setName(rs.getString("name"));
                film.getDirectors().add(director);
            }
        });
    }
}

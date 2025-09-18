package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> mapper = (rs, rowNum) -> {
        Director director = new Director();
        director.setId(rs.getLong("id"));
        director.setName(rs.getString("name"));
        return director;
    };

    @Override
    public Collection<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors ORDER BY id", mapper);
    }

    @Override
    public Optional<Director> getById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT * FROM directors WHERE id = ?", mapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Director addDirector(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO directors (name) VALUES (?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKeyAs(Long.class));
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update("UPDATE directors SET name = ? WHERE id = ?",
                director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Long id) {
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", id);
    }
}

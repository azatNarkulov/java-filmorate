package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Mpa> mapper = (rs, rowNum) -> {
        int id = rs.getInt("id");
        String name = rs.getString("mpa_name");

        return new Mpa(id, name);
    };

    @Override
    public Collection<Mpa> getAllMpa() {
        String findAllQuery = "SELECT * FROM mpa ORDER BY id";
        return jdbcTemplate.query(findAllQuery, mapper);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        String findByIdQuery = "SELECT * FROM mpa WHERE id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(findByIdQuery, mapper, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}

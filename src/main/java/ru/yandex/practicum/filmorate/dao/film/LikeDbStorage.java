package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        String addLikeQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(addLikeQuery, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String deleteLikeQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteLikeQuery, filmId, userId);
    }

    @Override
    public Collection<Long> getLikesByFilmId(Long filmId) {
        String getLikesByFilmIdQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(getLikesByFilmIdQuery, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }
}

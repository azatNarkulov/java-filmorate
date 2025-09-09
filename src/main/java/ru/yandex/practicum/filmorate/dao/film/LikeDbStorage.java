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

    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKES_BY_FILM_ID_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public Collection<Long> getLikesByFilmId(Long filmId) {
        return jdbcTemplate.query(GET_LIKES_BY_FILM_ID_QUERY, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }
}

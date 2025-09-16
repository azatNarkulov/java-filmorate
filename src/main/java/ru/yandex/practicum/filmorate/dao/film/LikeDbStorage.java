package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.util.*;

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

    @Override
    public Set<Long> getLikesByUserId(Long userId) {
        String getLikesByUserIdQuery = "SELECT film_id FROM likes WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(getLikesByUserIdQuery, (rs, rowNum) -> rs.getLong("film_id"), userId));
    }

    @Override
    public Map<Long, Long> findUsersWithCommonLikes(Long userId) {
        String findUserWithCommonLikesQuery = "SELECT l2.user_id, COUNT(l2.film_id) AS common_count " +
                "FROM likes l1 " +
                "JOIN likes l2 ON l1.film_id = l2.film_id " +
                "WHERE l1.user_id = ? AND l2.user_id <> ? " +
                "GROUP BY l2.user_id";

        return jdbcTemplate.query(findUserWithCommonLikesQuery, (rs) -> {
            Map<Long, Long> resultMap = new HashMap<>();
            while (rs.next()) {
                resultMap.put(rs.getLong("user_id"), rs.getLong("common_count"));
            }
            return resultMap;
        }, userId, userId);
    }
}

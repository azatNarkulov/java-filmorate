package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.storage.film.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(Review review) {
        String sql = "INSERT INTO reviews (film_id, user_id, content, is_positive) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, review.getFilmId());
            ps.setLong(2, review.getUserId());
            ps.setString(3, review.getContent());
            ps.setBoolean(4, review.getIsPositive());
            return ps;
        }, keyHolder);
        review.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getId());
        return getById(review.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review getById(Long id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToReview, id);
    }

    @Override
    public List<Review> getByFilmId(Long filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId, count);
    }

    @Override
    public List<Review> getAll(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, count);
    }

    @Override
    public void addLike(Long reviewId, Long userId, boolean isLike) {
        if (hasLike(reviewId, userId)) {
            String sql = "UPDATE review_likes SET is_like = ? WHERE review_id = ? AND user_id = ?";
            jdbcTemplate.update(sql, isLike, reviewId, userId);
        } else {
            String sql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, reviewId, userId, isLike);
        }
        updateUseful(reviewId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    @Override
    public boolean hasLike(Long reviewId, Long userId) {
        String sql = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean getLikeType(Long reviewId, Long userId) {
        String sql = "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";
        Boolean isLike = jdbcTemplate.queryForObject(sql, Boolean.class, reviewId, userId);
        return isLike != null ? isLike : false;
    }

    private void updateUseful(Long reviewId) {
        String likesSql = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = true";
        Integer likes = jdbcTemplate.queryForObject(likesSql, Integer.class, reviewId);
        String dislikesSql = "SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND is_like = false";
        Integer dislikes = jdbcTemplate.queryForObject(dislikesSql, Integer.class, reviewId);
        Integer useful = (likes != null ? likes : 0) - (dislikes != null ? dislikes : 0);
        String updateSql = "UPDATE reviews SET useful = ? WHERE id = ?";
        jdbcTemplate.update(updateSql, useful, reviewId);
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        review.setFilmId(rs.getLong("film_id"));
        review.setUserId(rs.getLong("user_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.storage.film.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        return reviewStorage.add(review);
    }

    public Review updateReview(Review review) {
        if (reviewStorage.getById(review.getId()) == null) {
            throw new RuntimeException("Отзыв не найден");
        }
        return reviewStorage.update(review);
    }

    public void deleteReview(Long id) {
        reviewStorage.delete(id);
    }

    public Review getReviewById(Long id) {
        Review review = reviewStorage.getById(id);
        if (review == null) {
            throw new RuntimeException("Отзыв не найден");
        }
        return review;
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        if (count == null) count = 10;
        if (filmId != null) {
            return reviewStorage.getByFilmId(filmId, count);
        } else {
            return reviewStorage.getAll(count);
        }
    }

    public void addLike(Long reviewId, Long userId, boolean isLike) {
        getReviewById(reviewId);
        reviewStorage.addLike(reviewId, userId, isLike);
    }

    public void removeLike(Long reviewId, Long userId) {
        reviewStorage.removeLike(reviewId, userId);
    }
}

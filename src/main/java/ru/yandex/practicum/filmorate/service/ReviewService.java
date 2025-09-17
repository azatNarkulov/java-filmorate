package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.model.film.ReviewLike;
import ru.yandex.practicum.filmorate.model.user.Event;
import ru.yandex.practicum.filmorate.model.user.EventType;
import ru.yandex.practicum.filmorate.model.user.Operation;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.film.*;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public ReviewService(ReviewStorage reviewStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage,
                         EventService eventService) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    public Review addReview(Review review) {
        Optional<User> userOpt = userStorage.getById(review.getUserId());
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + review.getUserId() + " не найден");
        }

        Optional<Film> filmOpt = filmStorage.getById(review.getFilmId());
        if (filmOpt.isEmpty()) {
            throw new NotFoundException("Фильм с ID " + review.getFilmId() + " не найден");
        }

        Review createdReview = reviewStorage.add(review);
        eventService.createEvent(new Event(createdReview.getUserId(), EventType.REVIEW, Operation.ADD, createdReview.getId()));

        return createdReview;
    }

    public Review updateReview(Review review) {
        if (reviewStorage.getById(review.getId()) == null) {
            throw new NotFoundException("Отзыв не найден");
        }
        Review updatedReview = reviewStorage.update(review);
        eventService.createEvent(new Event(updatedReview.getUserId(), EventType.REVIEW, Operation.UPDATE, updatedReview.getId()));

        return updatedReview;
    }

    public void deleteReview(Long id) {
        Review review = getReviewById(id);
        eventService.createEvent(new Event(review.getUserId(), EventType.REVIEW, Operation.REMOVE, id));

        reviewStorage.delete(id);
    }

    public Review getReviewById(Long id) {
        log.info("Получение отзыва по ID: {}", id);
        try {
            Review review = reviewStorage.getById(id);
            if (review == null) {
                throw new NotFoundException("Отзыв с ID " + id + " не найден");
            }
            return review;
        } catch (Exception e) {
            log.warn("Ошибка при получении отзыва с ID {}: {}", id, e.getMessage());
            throw new NotFoundException("Отзыв с ID " + id + " не найден");
        }
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

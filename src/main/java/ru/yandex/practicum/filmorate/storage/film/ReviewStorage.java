package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.model.film.ReviewLike;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review add(Review review);
    Review update(Review review);
    void delete(Long id);
    Review getById(Long id);
    List<Review> getByFilmId(Long filmId, int count);
    List<Review> getAll(int count);
    void addLike(Long reviewId, Long userId, boolean isLike);
    void removeLike(Long reviewId, Long userId);
    boolean hasLike(Long reviewId, Long userId);
    boolean getLikeType(Long reviewId, Long userId);
}
package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Long> getLikesByFilmId(Long filmId);
}

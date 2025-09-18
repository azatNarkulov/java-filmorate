package ru.yandex.practicum.filmorate.storage.film;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Long> getLikesByFilmId(Long filmId);

    Set<Long> getLikesByUserId(Long userId);

    Map<Long, Long> findUsersWithCommonLikes(Long userId);
}

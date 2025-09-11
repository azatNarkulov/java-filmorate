package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Set;

public interface FriendshipStorage {
    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> findAllFriends(Long userId);

    List<User> findCommonFriends(Long userId, Long otherId);

    Set<Long> findFriendIdsByUserId(Long userId);
}

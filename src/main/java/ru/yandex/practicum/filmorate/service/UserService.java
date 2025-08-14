package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    public User deleteUser(Long id) {
        return userStorage.deleteUser(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        User friend = userStorage.getById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);
        log.debug("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(userId);
        log.debug("Пользователь {} удалил пользователя {} из списка друзей", userId, friendId);
    }

    public List<User> getFriends(Long id) {
        return userStorage.getById(id).getFriendsId().stream()
                .map(userStorage::getById)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User otherUser = userStorage.getById(otherId);
        return userStorage.getById(userId).getFriendsId().stream()
                .filter(otherUser.getFriendsId()::contains)
                .map(userStorage::getById)
                .toList();
    }
}

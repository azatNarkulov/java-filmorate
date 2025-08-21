package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User getUserById(Long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public User createUser(User user) {
        setNameIfEmpty(user);
        log.debug("Добавляем пользователя: {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        getUserById(newUser.getId());
        setNameIfEmpty(newUser);
        log.debug("Обновляем данные пользователя: {}", newUser);
        return userStorage.updateUser(newUser);
    }

    public User deleteUser(Long id) {
        getUserById(id);
        log.debug("Удаляем пользователя: {}", userStorage.getById(id));
        return userStorage.deleteUser(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);
        log.debug("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(userId);
        log.debug("Пользователь {} удалил пользователя {} из списка друзей", userId, friendId);
    }

    public List<User> getFriends(Long id) {
        User user = getUserById(id);
        return user.getFriendsId().stream()
                .map(this::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);
        return user.getFriendsId().stream()
                .filter(otherUser.getFriendsId()::contains)
                .map(this::getUserById)
                .toList();
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            String login = user.getLogin();
            user.setName(login);
            log.debug("Имя пользователя {} было пустым, теперь используется логин {}", user.getId(), login);
        }
    }
}

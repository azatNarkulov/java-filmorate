package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.Event;
import ru.yandex.practicum.filmorate.model.user.EventType;
import ru.yandex.practicum.filmorate.model.user.Operation;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final EventStorage eventStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipStorage friendshipStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
        this.eventStorage = eventStorage;
    }

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

    public void deleteUser(Long id) {
        User user = getUserById(id);
        log.debug("Удаляем пользователя: {}", user);
        userStorage.deleteUser(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        friendshipStorage.addFriend(userId, friendId);
        log.debug("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
        createEvent(new Event(userId, EventType.FRIEND, Operation.ADD, friendId));
    }

    public void deleteFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        friendshipStorage.removeFriend(userId, friendId);
        log.debug("Пользователь {} удалил пользователя {} из списка друзей", userId, friendId);
        createEvent(new Event(userId, EventType.FRIEND, Operation.REMOVE, friendId));
    }

    public List<User> getFriends(Long id) {
        getUserById(id);
        return friendshipStorage.findAllFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        getUserById(userId);
        getUserById(otherId);
        return new ArrayList<>(friendshipStorage.findCommonFriends(userId, otherId));
    }

    public Event createEvent(Event event) {
        return eventStorage.create(event);
    }

    public List<Event> getEventsByUserId(Long userId) {
        return eventStorage.getEventsByUserId(userId);
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            String login = user.getLogin();
            user.setName(login);
            log.debug("Имя пользователя {} было пустым, теперь используется логин {}", user.getId(), login);
        }
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
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
        /*
        User friend = getUserById(friendId);*/

        /*if (user.getFriendsId().contains(friendId)) {
            return;
        }

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);*/

        User user = getUserById(userId);
        getUserById(friendId);

        user.getFriendsId().add(friendId);

        friendshipStorage.addFriend(userId, friendId);
//        friendshipStorage.addFriend(friendId, userId);
        log.debug("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        /*
        User friend = getUserById(friendId);

        if (user.getFriendsId().remove(friendId)) {
            friend.getFriendsId().remove(userId);
            friendshipStorage.removeFriend(userId, friendId);
            friendshipStorage.removeFriend(friendId, userId);
            log.debug("Пользователь {} удалил пользователя {} из списка друзей", userId, friendId);
        }*/

        User user = getUserById(userId);
        getUserById(friendId);

        user.getFriendsId().remove(friendId);

        friendshipStorage.removeFriend(userId, friendId);
//        friendshipStorage.removeFriend(friendId, userId);
        log.debug("Пользователь {} удалил пользователя {} из списка друзей", userId, friendId);
    }

    public List<User> getFriends(Long id) {
        getUserById(id);
        return friendshipStorage.findAllFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        /*getUserById(userId);
        getUserById(otherId);
        return friendshipStorage.findCommonFriends(userId, otherId);*/

        /*User user = getUserById(userId);
        User otherUser = getUserById(otherId);
        Set<Long> userFriends = Optional.ofNullable(user.getFriendsId())
                .orElse(Collections.emptySet());
        Set<Long> otherUserFriends = Optional.ofNullable(otherUser.getFriendsId())
                .orElse(Collections.emptySet());

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());*/

        getUserById(userId);
        getUserById(otherId);
        return new ArrayList<>(friendshipStorage.findCommonFriends(userId, otherId));
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            String login = user.getLogin();
            user.setName(login);
            log.debug("Имя пользователя {} было пустым, теперь используется логин {}", user.getId(), login);
        }
    }
}

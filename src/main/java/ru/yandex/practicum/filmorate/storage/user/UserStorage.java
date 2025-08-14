package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    User deleteUser(Long id);

    Collection<User> getAllUsers();

    User getById(Long id);
}

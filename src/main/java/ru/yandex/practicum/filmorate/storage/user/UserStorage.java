package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    Collection<User> getAllUsers();

    Optional<User> getById(Long id);
}

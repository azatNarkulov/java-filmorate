package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User addUser(User user) {
        log.debug("Добавляем пользователя: {}", user);
        setNameIfEmpty(user);

        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь не найден");
        }
        log.debug("Обновляем данные пользователя: {}", newUser);

        setNameIfEmpty(newUser);

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User deleteUser(Long id) {
        User user = users.remove(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        log.debug("Удаляем пользователя: {}", user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            String login = user.getLogin();
            user.setName(login);
            log.debug("Имя пользователя {} было пустым, теперь используется логин {}", user.getId(), login);
        }
    }

    private long generateId() {
        return id++;
    }
}

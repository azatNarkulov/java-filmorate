package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final HashMap<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug("Добавляем пользователя: {}", user);

        setNameIfEmpty(user);

        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь " + newUser.getId() + " не найден");
        }
        log.debug("Обновляем данные пользователя: {}", newUser);

        setNameIfEmpty(newUser);

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя было пустым, теперь используется логин");
        }
    }

    private long generateId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

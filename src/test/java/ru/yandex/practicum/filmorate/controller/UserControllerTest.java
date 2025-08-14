package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest { // проверяю работу сервиса
    private final UserStorage userStorage = new InMemoryUserStorage();
    private UserService userService;
    private User user;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userStorage);
        user = createValidUser();
    }

    @Test
    public void createUser_addUser_userIsValid() {
        User addedUser = userService.createUser(user);

        assertNotNull(addedUser);
        assertEquals(1, addedUser.getId());
    }

    @Test
    public void setNameIfEmpty_setLoginInsteadOfName_nameIsEmpty() {
        user.setName("");

        assertEquals(user.getLogin(), userService.createUser(user).getName());
    }

    @Test
    public void updateUser_updateUser_userIsValid() {
        userService.createUser(user);
        user.setName("Updated user");
        User updatedUser = userService.updateUser(user);

        assertEquals(user.getName(), updatedUser.getName());
    }

    @Test
    public void deleteUser_deleteUser() {
        userService.createUser(user);
        userService.deleteUser(user.getId());
        assertEquals(userService.getAllUsers().size(), 0);
    }

    @Test
    public void getAllUsers_getUsers() {
        userService.createUser(user);

        User anotherUser = new User();
        anotherUser.setEmail("anothervalidmail@yandex.ru");
        anotherUser.setLogin("anotherValidLogin");
        anotherUser.setName("Firstname Lastname");
        anotherUser.setBirthday(LocalDate.of(2000, 12, 14));
        userService.createUser(anotherUser);

        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    public void getUserById_getUserById() {
        userService.createUser(user);
        User receivedUser = userService.getUserById(user.getId());
        assertEquals(user.getId(), receivedUser.getId());
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("validmail@yandex.ru");
        user.setLogin("validLogin");
        user.setName("Firstname Lastname");
        user.setBirthday(LocalDate.of(2002, 3, 3));

        return user;
    }
}

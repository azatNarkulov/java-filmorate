package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController userController;
    private User user;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        user = createValidUser();
    }

    @Test
    public void addUser_addUser_userIsValid() {
        User addedUser = userController.addUser(user);

        assertNotNull(addedUser);
        assertEquals(1, addedUser.getId());
    }

    @Test
    public void setNameIfEmpty_setLoginInsteadOfName_nameIsEmpty() {
        user.setName("");

        assertEquals(user.getLogin(), userController.addUser(user).getName());
    }

    @Test
    public void updateUser_updateUser_userIsValid() {
        userController.addUser(user);
        user.setName("Updated user");
        User updatedUser = userController.updateUser(user);

        assertEquals(user.getName(), updatedUser.getName());
    }

    @Test
    public void getUsers_getUsers() {
        userController.addUser(user);

        User anotherUser = new User();
        anotherUser.setEmail("anothervalidmail@yandex.ru");
        anotherUser.setLogin("anotherValidLogin");
        anotherUser.setName("Firstname Lastname");
        anotherUser.setBirthday(LocalDate.of(2000, 12, 14));
        userController.addUser(anotherUser);

        assertEquals(2, userController.getUsers().size());
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

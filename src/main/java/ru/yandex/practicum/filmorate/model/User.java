package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    String email;

    @NotBlank(message = "Логин не может быть пустым")
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}

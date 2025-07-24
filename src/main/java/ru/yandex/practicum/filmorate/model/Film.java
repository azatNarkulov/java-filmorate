package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания – 200 символов")
    String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    @Past(message = "Дата релиза должна быть в прошлом")
    LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть пустой")
    @Positive(message = "Продолжительность фильма может быть только положительным числом")
    /*изначально у меня было Duration, но тесты не проходили, поменял на int – всё прошло
    не подскажете, это норм? Или нужно поменять на Duration?
    с Duration как будто код будет выглядеть сложнее*/
    int duration;
}

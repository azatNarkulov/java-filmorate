package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = ValidReleaseDateValidator.class)
public @interface ValidReleaseDate {
    String message() default "Дата релиза не может быть раньше 28 декабря 1895 года";

    // я до конца не понимаю значение двух нижних строк, но без них тесты в postman не проходят
    // взял их из других нотаций
    // это нормально? Не знаете случаем, будем ли мы проходить их в будущем?
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

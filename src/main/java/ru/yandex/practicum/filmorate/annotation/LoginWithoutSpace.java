package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.LoginWithoutSpaceValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = LoginWithoutSpaceValidator.class)
public @interface LoginWithoutSpace {
    String message() default "Логин не может содержать пробелы";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

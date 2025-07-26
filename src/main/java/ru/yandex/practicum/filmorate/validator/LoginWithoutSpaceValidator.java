package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.LoginWithoutSpace;

public class LoginWithoutSpaceValidator implements ConstraintValidator<LoginWithoutSpace, String> {

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        return !login.contains(" ");
    }
}

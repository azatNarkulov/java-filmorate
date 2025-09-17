package ru.yandex.practicum.filmorate.model.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Review {
    @JsonProperty("reviewId")
    private Long id;

    @NotNull(message = "userId не может быть null")
    private Long userId;

    @NotNull(message = "filmId не может быть null")
    private Long filmId;

    @NotBlank(message = "content не может быть пустым")
    private String content;

    @NotNull(message = "isPositive не может быть null")
    private Boolean isPositive;

    private Integer useful = 0;
}


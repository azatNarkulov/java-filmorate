package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"reviewId", "userId"})
public class ReviewLike {
    private Long reviewId;
    private Long userId;
    private boolean isUseful;
}
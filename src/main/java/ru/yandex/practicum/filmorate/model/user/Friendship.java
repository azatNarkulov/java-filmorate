package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Friendship {
    private Long userId1;
    private Long userId2;
    private FriendshipStatus status;
}

package ru.yandex.practicum.filmorate.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor @Getter @Setter
public class Friendship {
    private Long userId1;
    private Long userId2;
    private FriendshipStatus status;
}

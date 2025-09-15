package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.Event;

import java.util.List;

public interface EventStorage {
    Event create(Event event);

    List<Event> getEventsByUserId(Long userId);
}

package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.Event;
import ru.yandex.practicum.filmorate.storage.user.EventStorage;

import java.util.List;

@Service
public class EventService {
    private final EventStorage eventStorage;

    public EventService(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public Event createEvent(Event event) {
        return eventStorage.create(event);
    }

    public List<Event> getEventsByUserId(Long userId) {
        return eventStorage.getEventsByUserId(userId);
    }
}

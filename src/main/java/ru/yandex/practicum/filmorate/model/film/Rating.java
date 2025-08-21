package ru.yandex.practicum.filmorate.model.film;

import lombok.Getter;

@Getter
public enum Rating {
    G("G"), // нет возрастных ограничений
    PG("PG"), // детям рекомендуется смотреть фильм с родителями
    PG_13("PG-13"), // детям до 13 лет просмотр не желателен
    R("R"), // лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
    NC_17("NC-17"); // лицам до 18 лет просмотр запрещён

    private final String label;

    Rating(String label) {
        this.label = label;
    }
}

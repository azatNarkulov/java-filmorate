# java-filmorate

Схема базы данных (таблицы хранения пользователей и фильмов):

![Схема данных](/images/ER-диаграмма.png)


### Ниже приведены примеры SQL-запросов

1) Найти все фильмы с рейтингом PG-13:

```sql
SELECT *
FROM films
WHERE rating = 'PG-13';
```

2) Посмотреть друзей пользователя с id = 8:

```sql
SELECT *
FROM friendships AS f
JOIN users AS u ON f.user_id2 = u.id
WHERE f.user_id1 = 8
  AND f.status = 'CONFIRMED';
```

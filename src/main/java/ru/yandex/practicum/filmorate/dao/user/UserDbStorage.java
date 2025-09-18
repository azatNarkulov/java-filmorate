package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendshipStorage;

    @Override
    public User addUser(User user) {
        String insertQuery = "INSERT INTO users(email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            user.setId(id);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        String updateQuery = "UPDATE users SET email = ?, login = ?, " +
                "name = ?, birthday = ? WHERE id = ?";
        int updated = jdbcTemplate.update(updateQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        return user;
    }

    @Override
    public void deleteUser(Long id) {
        String deleteQuery = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteQuery, id);
    }

    @Override
    public Collection<User> getAllUsers() {
        String findAllQuery = "SELECT * FROM users";
        return jdbcTemplate.query(findAllQuery, mapper());
    }

    @Override
    public Optional<User> getById(Long id) {
        String findByIdQuery = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(findByIdQuery, mapper(), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    private RowMapper<User> mapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());

            Set<Long> friends = friendshipStorage.findFriendIdsByUserId(user.getId());
            user.setFriendsId(friends);

            return user;
        };
    }
}

package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.FriendshipStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> mapper = ((rs, rowNum) -> {
       User user = new User();
       user.setId(rs.getLong("id"));
       user.setEmail(rs.getString("email"));
       user.setLogin(rs.getString("login"));
       user.setName(rs.getString("name"));
       user.setBirthday(rs.getDate("birthday").toLocalDate());
       return user;
    });

    @Override
    public void addFriend(Long userId, Long friendId) {
        String checkUserQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkUserQuery, Integer.class, userId);
        Integer friendCount = jdbcTemplate.queryForObject(checkUserQuery, Integer.class, friendId);

        if (userCount == 0 || friendCount == 0) {
            throw new NotFoundException("Пользователь не найден");
        }

        String addFriendQuery = "INSERT INTO friendship(user_id, friend_id, confirmed) VALUES(?, ?, false)";
        jdbcTemplate.update(addFriendQuery, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String deleteFriendQuery = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteFriendQuery, userId, friendId);
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        String findAllFriendsQuery = "SELECT * " +
                "FROM friendship f " +
                "INNER JOIN users u ON u.id = f.friend_id " +
                "WHERE f.user_id = ? " +
                "ORDER BY u.id";
        return jdbcTemplate.query(findAllFriendsQuery, mapper, userId);
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long otherId) {
        String findCommonFriendsQuery = "SELECT u.* FROM users u " +
                "JOIN friendship f1 ON u.id = f1.friend_id AND f1.user_id = ? " +
                "JOIN friendship f2 ON u.id = f2.friend_id AND f2.user_id = ?";
        return jdbcTemplate.query(findCommonFriendsQuery, mapper, userId, otherId);
    }

    @Override
    public Set<Long> findFriendIdsByUserId(Long userId) {
        String findFriendsIdsQuery = "SELECT friend_id FROM friendship WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(findFriendsIdsQuery, (rs, rowNum) -> rs.getLong("friend_id"), userId));
    }
}

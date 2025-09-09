package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
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
       user.setId(rs.getLong("user_id"));
       user.setEmail(rs.getString("email"));
       user.setLogin(rs.getString("login"));
       user.setName(rs.getString("name"));
       user.setBirthday(rs.getDate("birthday").toLocalDate());
       return user;
    });

    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendship(user_id, friend_id) VALUES(?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT * " +
            "FROM friendship f " +
            "INNER JOIN users u ON u.id = f.friend_id " +
            "WHERE f.user_id = ? " +
            "ORDER BY u.id";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT * " +
            "FROM friendship f " +
            "INNER JOIN users u ON u.id = f.friend_id " +
            "WHERE f.user_id = ? " +
            "AND f.friend_id IN (" +
                 "SELECT friend_id " +
                 "FROM friendship " +
                 "WHERE user_id = ?" +
            ") " +
            "ORDER BY u.id";

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update(ADD_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update(DELETE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        return jdbcTemplate.query(FIND_ALL_FRIENDS_QUERY, mapper, userId);
    }

    @Override
    public Set<User> findCommonFriends(Long userId, Long otherId) {
        return new HashSet<>(jdbcTemplate.query(FIND_COMMON_FRIENDS_QUERY, mapper, userId, otherId));
    }
}

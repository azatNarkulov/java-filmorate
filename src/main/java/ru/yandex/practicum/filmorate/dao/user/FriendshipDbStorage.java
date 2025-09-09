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
       user.setId(rs.getLong("id"));
       user.setEmail(rs.getString("email"));
       user.setLogin(rs.getString("login"));
       user.setName(rs.getString("name"));
       user.setBirthday(rs.getDate("birthday").toLocalDate());
       return user;
    });

    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendship(user_id, friend_id, confirmed) VALUES(?, ?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String CHECK_FRIENDSHIP_QUERY = "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String CONFIRM_FRIENDSHIP_QUERY = "UPDATE friendship SET confirmed = true WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_FRIEND_IDS_QUERY = "SELECT friend_id FROM friendship WHERE user_id = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT u.* FROM users u " +
            "JOIN friendship f1 ON u.id = f1.friend_id AND f1.user_id = ? " +
            "JOIN friendship f2 ON u.id = f2.friend_id AND f2.user_id = ?";
    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT * " +
            "FROM friendship f " +
            "INNER JOIN users u ON u.id = f.friend_id " +
            "WHERE f.user_id = ? " +
            "ORDER BY u.id";


    @Override
    public void addFriend(Long userId, Long friendId) {
        Integer count = jdbcTemplate.queryForObject(CHECK_FRIENDSHIP_QUERY, Integer.class, friendId, userId);
        boolean confirmed = count != null && count > 0;

        if (confirmed) {
            jdbcTemplate.update(CONFIRM_FRIENDSHIP_QUERY, friendId, userId);
            jdbcTemplate.update(CONFIRM_FRIENDSHIP_QUERY, userId, friendId);
        } else {
            jdbcTemplate.update(ADD_FRIEND_QUERY, userId, friendId, false);
        }
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
    public List<User> findCommonFriends(Long userId, Long otherId) {
        return jdbcTemplate.query(FIND_COMMON_FRIENDS_QUERY, mapper, userId, otherId);
    }

    @Override
    public Set<Long> findFriendIdsByUserId(Long userId) {
        return new HashSet<>(jdbcTemplate.query(FIND_FRIEND_IDS_QUERY, (rs, rowNum) -> rs.getLong("friend_id"), userId));
    }
}

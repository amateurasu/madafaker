package com.viettel.ems.model.cache;

import com.viettel.ems.model.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class UserCache extends ConcurrentHashMap<String, User> {

    private final JdbcTemplate jdbc;
    private final User.Mapper mapper;

    @Override
    public User get(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("User cache's key must be an instance of String!");
        }

        var user = super.get(key);
        return user == null ? fetchFromDb((String) key) : user;
    }

    private User fetchFromDb(String key) {
        var user = jdbc.queryForObject("SELECT * FROM user WHERE id = ?", mapper, key);
        return user == null ? null : put(key, user);
    }
}

package com.stone.db.proxy.mapper;

import com.stone.db.proxy.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Stony
 *         Created Date : 2016/4/18  11:16
 */
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getTimestamp("birthday"));
        return user;
    }
}

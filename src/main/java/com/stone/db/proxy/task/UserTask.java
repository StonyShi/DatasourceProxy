package com.stone.db.proxy.task;

import com.stone.db.proxy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ShiHui on 2016/1/10.
 */
@Component
public class UserTask {

    @Autowired
    @Qualifier("taskJdbcTemplate")
    private JdbcTemplate taskJdbcTemplate;

    public int addUser(User user){
        String sql = "INSERT INTO users(name,birthday) VALUES (?,?)";
        return taskJdbcTemplate.update(sql, user.getName(),user.getBirthday());
    }

    public List<User> getUsers(){
        String sql = "SELECT id,name,birthday FROM users";
        return taskJdbcTemplate.query(sql, new UserRowMapper());
    }

    public void addTx() {
        throw new RuntimeException("xxxx");
    }


    static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getTimestamp("birthday"));
            return user;
        }
    }

}

package com.stone.db.proxy.dao;

import com.stone.db.proxy.mapper.UserRowMapper;
import com.stone.db.proxy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ShiHui on 2016/1/10.
 */
@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int addUser(User user){
        String sql = "INSERT INTO users(name,birthday) VALUES (?,?)";
        return jdbcTemplate.update(sql, user.getName(),user.getBirthday());
    }

    public List<User> getUsers(){
        String sql = "SELECT id,name,birthday FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public User getUser(Integer id) {
        String sql = "SELECT id,name,birthday FROM users Where id = ?";
        List<User> list = jdbcTemplate.query(sql,new Object[]{id},new UserRowMapper());
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}

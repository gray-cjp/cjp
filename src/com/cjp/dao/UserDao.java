package com.cjp.dao;

import com.cjp.domain.User;
import com.cjp.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

public class UserDao {
    public int register(User user) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into user values (?,?,?,?,?,?,?,?,?,?)";
        int row = qr.update(sql, user.getUid(), user.getUsername(), user.getPassword(), user.getName(), user.getEmail(), user.getTelephone(),
                user.getBirthday(), user.getSex(), user.getState(), user.getCode());
        return row;
    }

    public void active(String activeCode) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="update user set state=? where code=?";
        qr.update(sql,1,activeCode);
    }

    public Long checkUsername(String username) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="select count(*) from user where username=?";
        Long row = (Long) qr.query(sql,new ScalarHandler(),username);
        return row;
    }

    public User userLogin(String username, String password) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="select * from user where username=? and password=?";
        return qr.query(sql,new BeanHandler<User>(User.class),username,password);
    }
}

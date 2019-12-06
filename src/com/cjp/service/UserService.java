package com.cjp.service;

import com.cjp.dao.UserDao;
import com.cjp.domain.User;

import java.sql.SQLException;

public class UserService {
    UserDao dao =new UserDao();
    public boolean register(User user) {
        int row =0;
        try {
            row = dao.register(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row>0?true:false;
    }

    public void active(String activeCode) {
        try {
            dao.active(activeCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUsername(String username) {
        Long row =null;
        try {
            row = dao.checkUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row>0?true:false;
    }

    public User userLogin(String username, String password) {
        User user =null;
        try {
            user = dao.userLogin(username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}

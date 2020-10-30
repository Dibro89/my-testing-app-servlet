package ua.training.mytestingapp.dao.impl;

import ua.training.mytestingapp.dao.UserDao;

import java.sql.Connection;

public class JdbcUserDao extends JdbcDao implements UserDao {

    protected JdbcUserDao(Connection connection) {
        super(connection);
    }

    @Override
    public void createTable() {
    }
}

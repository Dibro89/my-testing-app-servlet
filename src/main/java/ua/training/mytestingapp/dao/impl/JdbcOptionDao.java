package ua.training.mytestingapp.dao.impl;

import ua.training.mytestingapp.dao.OptionDao;

import java.sql.Connection;

public class JdbcOptionDao extends JdbcDao implements OptionDao {

    public JdbcOptionDao(Connection connection) {
        super(connection);
    }

    @Override
    public void createTable() {
    }
}

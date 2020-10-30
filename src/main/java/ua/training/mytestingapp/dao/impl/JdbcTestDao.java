package ua.training.mytestingapp.dao.impl;

import ua.training.mytestingapp.dao.TestDao;

import java.sql.Connection;

public class JdbcTestDao extends JdbcDao implements TestDao {

    public JdbcTestDao(Connection connection) {
        super(connection);
    }

    @Override
    public void createTable() {
    }
}

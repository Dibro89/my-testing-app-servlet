package ua.training.mytestingapp.dao.impl;

import ua.training.mytestingapp.dao.AttemptDao;

import java.sql.Connection;

public class JdbcAttemptDao extends JdbcDao implements AttemptDao {

    public JdbcAttemptDao(Connection connection) {
        super(connection);
    }

    @Override
    public void createTable() {
    }
}

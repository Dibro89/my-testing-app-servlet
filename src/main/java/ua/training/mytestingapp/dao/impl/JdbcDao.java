package ua.training.mytestingapp.dao.impl;

import java.sql.Connection;

public abstract class JdbcDao {

    protected final Connection connection;

    protected JdbcDao(Connection connection) {
        this.connection = connection;
    }

    public abstract void createTable();
}

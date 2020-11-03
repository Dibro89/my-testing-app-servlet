package ua.training.mytestingapp.dao;

import java.sql.Connection;

public abstract class AbstractJdbcDao {

    protected Connection connection;

    protected AbstractJdbcDao(Connection connection) {
        this.connection = connection;
    }
}

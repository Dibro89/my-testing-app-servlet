package ua.training.mytestingapp.dao.impl;

import ua.training.mytestingapp.dao.DaoManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcDaoManager implements DaoManager, AutoCloseable {

    private final Connection connection;

    public JdbcDaoManager(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    public void createTables() {
        createUserDao().createTable();
        createTestDao().createTable();
        createQuestionDao().createTable();
        createOptionDao().createTable();
        createAttemptDao().createTable();
    }

    @Override
    public void beginTransaction() {
    }

    @Override
    public JdbcUserDao createUserDao() {
        return new JdbcUserDao(connection);
    }

    @Override
    public JdbcTestDao createTestDao() {
        return new JdbcTestDao(connection);
    }

    @Override
    public JdbcQuestionDao createQuestionDao() {
        return new JdbcQuestionDao(connection);
    }

    @Override
    public JdbcOptionDao createOptionDao() {
        return new JdbcOptionDao(connection);
    }

    @Override
    public JdbcAttemptDao createAttemptDao() {
        return new JdbcAttemptDao(connection);
    }

    @Override
    public void endTransaction() {
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}

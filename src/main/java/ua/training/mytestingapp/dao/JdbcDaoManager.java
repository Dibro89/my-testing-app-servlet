package ua.training.mytestingapp.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JdbcDaoManager implements AutoCloseable {

    private final Connection connection;

    public JdbcDaoManager(DataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    public JdbcUserDao createUserDao() {
        return new JdbcUserDao(connection);
    }

    public JdbcTestDao createTestDao() {
        return new JdbcTestDao(connection);
    }

    public JdbcAttemptDao createAttemptDao() {
        return new JdbcAttemptDao(connection);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}

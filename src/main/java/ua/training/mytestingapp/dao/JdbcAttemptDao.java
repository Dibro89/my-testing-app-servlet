package ua.training.mytestingapp.dao;

import ua.training.mytestingapp.entity.Attempt;
import ua.training.mytestingapp.entity.Test;
import ua.training.mytestingapp.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcAttemptDao extends AbstractJdbcDao {

    public JdbcAttemptDao(Connection connection) {
        super(connection);
    }

    public void insert(Attempt attempt) throws SQLException {
        final String sql = "" +
            "insert into attempts (user_id, test_id, score) " +
            "values (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, attempt.getUser().getId());
            statement.setLong(2, attempt.getTest().getId());
            statement.setString(3, attempt.getScore());

            statement.executeUpdate();
        }
    }

    public List<Attempt> findByUserId(Long userId) throws SQLException {
        final String sql = "" +
            "select * from attempts " +
            "where user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Attempt> attempts = new ArrayList<>();
                while (resultSet.next()) {
                    attempts.add(mapToAttempt(resultSet));
                }
                return attempts;
            }
        }
    }

    private Attempt mapToAttempt(ResultSet resultSet) throws SQLException {
        Attempt attempt = new Attempt();

        User tmpUser = new User();
        tmpUser.setId(resultSet.getLong("user_id"));
        attempt.setUser(tmpUser);

        Test tmpTest = new Test();
        tmpTest.setId(resultSet.getLong("test_id"));
        attempt.setTest(tmpTest);

        attempt.setScore(resultSet.getString("score"));
        return attempt;
    }
}

package ua.training.mytestingapp.dao;

import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.util.PageInfo;

import java.sql.Date;
import java.sql.*;
import java.util.*;

public class JdbcUserDao extends AbstractJdbcDao {

    public JdbcUserDao(Connection connection) {
        super(connection);
    }

    public User insert(User user) throws SQLException {
        final String insertUserSql = "" +
            "insert into mta_users (username, password, display_name, registration_date, locked) " +
            "values (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertUserSql, new String[]{"id"})) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getDisplayName());
            statement.setDate(4, Date.valueOf(user.getRegistrationDate()));
            statement.setBoolean(5, user.isLocked());

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    user.setId(resultSet.getLong("id"));
                } else {
                    throw new SQLException();
                }
            }
        }

        insertRoles(user);

        return user;
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        final String selectUsersSql = "" +
            "select * from mta_users " +
            "where username = ?";

        User user;

        try (PreparedStatement statement = connection.prepareStatement(selectUsersSql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    user = mapToUser(resultSet);

                } else {

                    return Optional.empty();

                }
            }
        }

        final String selectRolesSql = "" +
            "select user_role from user_roles " +
            "where user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(selectRolesSql)) {
            statement.setLong(1, user.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                Set<String> roles = new HashSet<>();

                while (resultSet.next()) {
                    roles.add(resultSet.getString("user_role"));
                }

                user.setRoles(roles);
            }
        }

        return Optional.of(user);
    }

    public PageInfo<User> findAllByUsernameContainingPaged(String username, int page, int size) throws SQLException {
        List<User> users;
        int totalPages;

        final String selectSql = "" +
            "select * from mta_users " +
            (username != null ? "where lower(username) like lower(?) " : "") +
            "limit ? offset ?";

        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            if (username != null) {
                statement.setString(1, "%" + username + "%");
                statement.setInt(2, size);
                statement.setInt(3, page * size);
            } else {
                statement.setInt(1, size);
                statement.setInt(2, page * size);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                users = new ArrayList<>();

                while (resultSet.next()) {
                    users.add(mapToUser(resultSet));
                }
            }
        }

        final String countSql = "" +
            "select count(*) from mta_users " +
            (username != null ? "where lower(username) like lower(?) " : "");

        try (PreparedStatement statement = connection.prepareStatement(countSql)) {
            if (username != null) {
                statement.setString(1, username);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    totalPages = count / size + (count % size != 0 ? 1 : 0);
                } else {
                    throw new SQLException();
                }
            }
        }

        return new PageInfo<>(users, page, totalPages);
    }

    public boolean existsByUsername(String username) throws SQLException {
        final String sql = "" +
            "select id from mta_users " +
            "where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public void update(User user) throws SQLException {
        final String updateUserSql = "" +
            "update mta_users " +
            "set display_name = ?, password = ?, locked = ? " +
            "where id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateUserSql)) {
            statement.setString(1, user.getDisplayName());
            statement.setString(2, user.getPassword());
            statement.setBoolean(3, user.isLocked());
            statement.setLong(4, user.getId());

            statement.executeUpdate();
        }

        deleteRoles(user);
        insertRoles(user);
    }

    private void insertRoles(User user) throws SQLException {
        final String insertRolesSql = "" +
            "insert into user_roles (user_id, user_role) " +
            "values (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertRolesSql)) {
            for (String role : user.getRoles()) {
                statement.setLong(1, user.getId());
                statement.setString(2, role);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void deleteRoles(User user) throws SQLException {
        final String deleteRolesSql = "" +
            "delete from user_roles " +
            "where user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(deleteRolesSql)) {
            statement.setLong(1, user.getId());
            statement.executeUpdate();
        }
    }

    private static User mapToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setDisplayName(resultSet.getString("display_name"));
        user.setRegistrationDate(resultSet.getDate("registration_date").toLocalDate());
        user.setLocked(resultSet.getBoolean("locked"));
        return user;
    }
}

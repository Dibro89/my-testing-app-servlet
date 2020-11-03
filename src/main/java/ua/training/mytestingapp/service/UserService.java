package ua.training.mytestingapp.service;

import ua.training.mytestingapp.dao.JdbcDaoManager;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.util.PageInfo;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Optional;

public class UserService {

    public static final int USERS_PER_PAGE = 10;

    private final DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findByUsername(String username) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            return daoManager.createUserDao().findByUsername(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsByUsername(String username) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            return daoManager.createUserDao().existsByUsername(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: transaction?
    public User insert(User user) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            return daoManager.createUserDao().insert(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: transaction?
    public void update(User user) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            daoManager.createUserDao().update(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PageInfo<User> getUserList(String username, int page) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            return daoManager.createUserDao()
                .findAllByUsernameContainingPaged(username, page, USERS_PER_PAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createAdmin() {
        final String adminUsername = "admin";
        final String adminPassword = "admin";

        if (existsByUsername(adminUsername)) {
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPassword(adminPassword);
        admin.setDisplayName(adminUsername.toUpperCase());
        admin.setRegistrationDate(LocalDate.now());
        admin.addRole(User.ROLE_USER);
        admin.addRole(User.ROLE_ADMIN);

        insert(admin);
    }
}

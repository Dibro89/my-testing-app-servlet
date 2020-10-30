package ua.training.mytestingapp.service;

import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.util.PageInfo;

import javax.sql.DataSource;
import java.util.Optional;

public class UserService {

    private final DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PageInfo<User> getUserList(Optional<String> username, int page) {
        return null;
    }
}

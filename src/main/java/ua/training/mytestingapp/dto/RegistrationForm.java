package ua.training.mytestingapp.dto;

import ua.training.mytestingapp.entity.User;

public class RegistrationForm extends EditForm {

    private final String username;

    public RegistrationForm(String password, String displayName, boolean admin, String username) {
        super(password, displayName, admin);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public User toUser() {
        User user = new User();
        user.setUsername(getUsername());
        user.setPassword(getPassword());
        user.setDisplayName(getDisplayName());
        return user;
    }
}

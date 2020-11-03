package ua.training.mytestingapp.dto;

import ua.training.mytestingapp.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

public class UserRegistrationForm extends UserEditForm {

    private String username;

    public UserRegistrationForm() {
    }

    public UserRegistrationForm(HttpServletRequest request) {
        super(request);
        this.username = request.getParameter("username");
    }

    @Override
    public List<String> getErrors() {
        List<String> errors = super.getErrors();

        if (!username.matches("\\w{5,20}")) {
            errors.add("username");
        }

        return errors;
    }

    public User toUser() {
        User user = new User();
        user.setUsername(getUsername());
        user.setPassword(getPassword());
        user.setDisplayName(getDisplayName());
        user.setRegistrationDate(LocalDate.now());
        user.addRole(User.ROLE_USER);
        return user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

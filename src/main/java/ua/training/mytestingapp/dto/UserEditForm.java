package ua.training.mytestingapp.dto;

import ua.training.mytestingapp.controller.AbstractController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class UserEditForm {

    private String password;
    private String displayName;
    private boolean admin;

    public UserEditForm() {
    }

    public UserEditForm(HttpServletRequest request) {
        this.password = AbstractController.getParameter(request, "password");
        this.displayName = AbstractController.getParameter(request, "displayName");
        this.admin = AbstractController.getOptionalParameter(request, "admin").isPresent();
    }

    public List<String> getErrors() {
        List<String> errors = new ArrayList<>();

        if (password.length() < 6) {
            errors.add("password");
        }

        if (displayName.length() < 2) {
            errors.add("displayName");
        }

        return errors;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}

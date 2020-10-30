package ua.training.mytestingapp.dto;

public class EditForm {

    private final String password;
    private final String displayName;
    private final boolean admin;

    public EditForm(String password, String displayName, boolean admin) {
        this.password = password;
        this.displayName = displayName;
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return admin;
    }
}

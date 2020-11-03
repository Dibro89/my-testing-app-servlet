package ua.training.mytestingapp.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class User {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private Long id;
    private String username;
    private String password;
    private String displayName;
    private LocalDate registrationDate;
    private Set<String> roles;
    private boolean locked;

    public User() {
    }

    public void addRole(String role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public boolean isAdmin() {
        return roles != null && roles.contains(ROLE_ADMIN);
    }

    public void setAdmin(boolean admin) {
        if (admin) {
            addRole(ROLE_ADMIN);
        } else if (roles != null) {
            roles.remove(ROLE_ADMIN);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}

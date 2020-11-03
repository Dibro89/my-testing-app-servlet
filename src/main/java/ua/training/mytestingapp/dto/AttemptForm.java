package ua.training.mytestingapp.dto;

import ua.training.mytestingapp.entity.Test;
import ua.training.mytestingapp.entity.User;

import java.util.Map;

public class AttemptForm {

    private final User user;
    private final Test test;
    private final Map<String, String[]> parameterMap;

    public AttemptForm(User user, Test test, Map<String, String[]> parameterMap) {
        this.user = user;
        this.test = test;
        this.parameterMap = parameterMap;
    }

    public User getUser() {
        return user;
    }

    public Test getTest() {
        return test;
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }
}

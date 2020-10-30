package ua.training.mytestingapp.service;

import ua.training.mytestingapp.entity.Attempt;

import javax.sql.DataSource;

public class AttemptService {

    private final DataSource dataSource;

    public AttemptService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveAttempt(Attempt attempt) {
    }
}

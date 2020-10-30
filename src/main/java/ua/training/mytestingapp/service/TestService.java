package ua.training.mytestingapp.service;

import ua.training.mytestingapp.entity.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class TestService {

    private final DataSource dataSource;

    public TestService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<Test> findById(Long testId) {
        return null;
    }

    public List<Test> findAllBySubject(Optional<String> subject, String sort) {
        return null;
    }

    public List<String> findAllSubjects() {
        return null;
    }
}

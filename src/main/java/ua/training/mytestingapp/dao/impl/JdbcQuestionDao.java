package ua.training.mytestingapp.dao.impl;

import ua.training.mytestingapp.dao.QuestionDao;

import java.sql.Connection;

public class JdbcQuestionDao extends JdbcDao implements QuestionDao {

    public JdbcQuestionDao(Connection connection) {
        super(connection);
    }

    @Override
    public void createTable() {
    }
}

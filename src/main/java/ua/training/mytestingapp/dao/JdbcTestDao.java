package ua.training.mytestingapp.dao;

import ua.training.mytestingapp.entity.Option;
import ua.training.mytestingapp.entity.Question;
import ua.training.mytestingapp.entity.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTestDao extends AbstractJdbcDao {

    public JdbcTestDao(Connection connection) {
        super(connection);
    }

    public Test insert(Test test) throws SQLException {
        final String sql = "" +
            "insert into tests (name, subject, duration, difficulty, creation_date) " +
            "values (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            statement.setString(1, test.getName());
            statement.setString(2, test.getSubject());
            statement.setInt(3, test.getDuration());
            statement.setInt(4, test.getDifficulty());
            statement.setDate(5, Date.valueOf(test.getCreationDate()));

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    test.setId(resultSet.getLong("id"));
                } else {
                    throw new SQLException();
                }
            }
        }

        insertQuestions(test.getId(), test.getQuestions());

        return test;
    }

    public void insertQuestions(Long testId, List<Question> questions) throws SQLException {
        final String sql = "" +
            "insert into questions (test_id, text, multiple) " +
            "values (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            for (Question question : questions) {
                statement.setLong(1, testId);
                statement.setString(2, question.getText());
                statement.setBoolean(3, question.isMultiple());
                statement.addBatch();
            }

            statement.executeBatch();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                int i = 0;
                while (resultSet.next()) {
                    questions.get(i).setId(resultSet.getLong("id"));
                    i++;
                }
                if (i != questions.size()) {
                    throw new SQLException();
                }
            }
        }

        for (Question question : questions) {
            insertOptions(question.getId(), question.getOptions());
        }
    }

    private void insertOptions(Long questionId, List<Option> options) throws SQLException {
        final String sql = "" +
            "insert into options (question_id, text, correct) " +
            "values (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            for (Option option : options) {
                statement.setLong(1, questionId);
                statement.setString(2, option.getText());
                statement.setBoolean(3, option.isCorrect());
                statement.addBatch();
            }

            statement.executeBatch();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                int i = 0;
                while (resultSet.next()) {
                    options.get(i).setId(resultSet.getLong("id"));
                    i++;
                }
                if (i != options.size()) {
                    throw new SQLException();
                }
            }
        }
    }

    public Optional<Test> findById(Long id) throws SQLException {
        final String selectTestSql = "" +
            "select * from tests " +
            "where id = ?";

        Test test;

        try (PreparedStatement statement = connection.prepareStatement(selectTestSql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    test = mapToTest(resultSet);
                } else {
                    return Optional.empty();
                }
            }
        }

        test.setQuestions(getQuestions(test.getId()));

        return Optional.of(test);
    }

    public List<Test> findAllBySubject(String subject, String sort) throws SQLException {
        final String order = sort.substring("by".length()).toLowerCase();

        final String sql = "" +
            "select * from tests " +
            (subject != null ? "where subject = ? " : "") +
            "order by " + order;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (subject != null) {
                statement.setString(1, subject);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Test> tests = new ArrayList<>();
                while (resultSet.next()) {
                    tests.add(mapToTest(resultSet));
                }
                return tests;
            }
        }
    }

    public List<String> findAllSubjects() throws SQLException {
        final String sql = "" +
            "select distinct subject from tests";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> subjects = new ArrayList<>();
                while (resultSet.next()) {
                    subjects.add(resultSet.getString("subject"));
                }
                return subjects;
            }
        }
    }

    public boolean existsById(Long id) throws SQLException {
        final String sql = "" +
            "select id from tests " +
            "where id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public void update(Test test) throws SQLException {
        deleteById(test.getId());
        insert(test);
    }

    public void deleteById(Long id) throws SQLException {
        final String sql = "" +
            "delete from tests " +
            "where id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public List<Question> getQuestions(Long testId) throws SQLException {
        final String selectQuestionsSql = "" +
            "select * from questions " +
            "where test_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(selectQuestionsSql)) {
            statement.setLong(1, testId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Question> questions = new ArrayList<>();
                while (resultSet.next()) {
                    Question question = mapToQuestion(resultSet);
                    question.setOptions(getOptions(question.getId()));
                    questions.add(question);
                }
                return questions;
            }
        }
    }

    public List<Option> getOptions(Long questionId) throws SQLException {
        final String sql = "" +
            "select * from options " +
            "where question_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, questionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Option> options = new ArrayList<>();
                while (resultSet.next()) {
                    options.add(mapToOption(resultSet));
                }
                return options;
            }
        }
    }

    private static Test mapToTest(ResultSet resultSet) throws SQLException {
        Test test = new Test();
        test.setId(resultSet.getLong("id"));
        test.setName(resultSet.getString("name"));
        test.setSubject(resultSet.getString("subject"));
        test.setDuration(resultSet.getInt("duration"));
        test.setDifficulty(resultSet.getInt("difficulty"));
        test.setCreationDate(resultSet.getDate("creation_date").toLocalDate());
        return test;
    }

    private static Question mapToQuestion(ResultSet resultSet) throws SQLException {
        Question question = new Question();
        question.setId(resultSet.getLong("id"));
        question.setText(resultSet.getString("text"));
        question.setMultiple(resultSet.getBoolean("multiple"));
        return question;
    }

    private Option mapToOption(ResultSet resultSet) throws SQLException {
        Option option = new Option();
        option.setId(resultSet.getLong("id"));
        option.setText(resultSet.getString("text"));
        option.setCorrect(resultSet.getBoolean("correct"));
        return option;
    }
}

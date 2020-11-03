package ua.training.mytestingapp.service;

import ua.training.mytestingapp.dao.JdbcDaoManager;
import ua.training.mytestingapp.entity.Option;
import ua.training.mytestingapp.entity.Question;
import ua.training.mytestingapp.entity.Test;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TestService {

    private final DataSource dataSource;

    public TestService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // TODO: transaction?
    public void insert(Test test) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            daoManager.createTestDao().insert(test);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Test> findById(Long id) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            return daoManager.createTestDao().findById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Test> findAllBySubject(String subject, String sort) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            return daoManager.createTestDao().findAllBySubject(subject, sort);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> findAllSubjects() {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            return daoManager.createTestDao().findAllSubjects();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsById(Long id) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            return daoManager.createTestDao().existsById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: transaction?
    public void update(Test test) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            daoManager.createTestDao().update(test);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: transaction?
    public void deleteById(Long id) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            daoManager.createTestDao().deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createSampleTest() {
        if (existsById(1L)) {
            return;
        }

        Test test = new Test();
        test.setName("Sample test");
        test.setSubject("Sample subject");
        test.setDuration(30);
        test.setDifficulty(5);
        test.setCreationDate(LocalDate.now());

        Question question1 = new Question();
        question1.setText("Sample question?");
        question1.setMultiple(false);

        Option option11 = new Option();
        option11.setText("Sample option");
        option11.setCorrect(true);

        Option option12 = new Option();
        option12.setText("Sample option");
        option12.setCorrect(false);

        Option option13 = new Option();
        option13.setText("Sample option");
        option13.setCorrect(false);

        question1.setOptions(List.of(option11, option12, option13));

        Question question2 = new Question();
        question2.setText("Sample question?");
        question2.setMultiple(true);

        Option option21 = new Option();
        option21.setText("Sample option");
        option21.setCorrect(false);

        Option option22 = new Option();
        option22.setText("Sample option");
        option22.setCorrect(true);

        Option option23 = new Option();
        option23.setText("Sample option");
        option23.setCorrect(true);

        question2.setOptions(List.of(option21, option22, option23));

        test.setQuestions(List.of(question1, question2));

        insert(test);
    }
}

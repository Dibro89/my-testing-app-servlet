package ua.training.mytestingapp.service;

import ua.training.mytestingapp.dao.JdbcDaoManager;
import ua.training.mytestingapp.dao.JdbcTestDao;
import ua.training.mytestingapp.dto.AttemptForm;
import ua.training.mytestingapp.entity.Attempt;
import ua.training.mytestingapp.entity.Option;
import ua.training.mytestingapp.entity.Question;
import ua.training.mytestingapp.entity.Test;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AttemptService {

    private final DataSource dataSource;

    public AttemptService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Attempt attempt) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            daoManager.createAttemptDao().insert(attempt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Attempt> findByUserId(Long userId) {
        try (JdbcDaoManager daoManager = new JdbcDaoManager(dataSource)) {
            List<Attempt> attempts = daoManager.createAttemptDao().findByUserId(userId);

            JdbcTestDao testDao = daoManager.createTestDao();
            Map<Long, Test> cache = new HashMap<>();

            for (Attempt attempt : attempts) {
                Long testId = attempt.getTest().getId();

                if (!cache.containsKey(testId)) {
                    cache.put(testId, testDao.findById(testId).orElseThrow());
                }

                attempt.setTest(cache.get(testId));
            }

            return attempts;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Attempt checkAttempt(AttemptForm form) {
        Attempt attempt = new Attempt();

        attempt.setUser(form.getUser());
        attempt.setTest(form.getTest());

        long count = form.getParameterMap().entrySet().stream()
            .filter(entry -> checkQuestion(form.getTest(), entry))
            .count();

        String score = count + "/" + form.getTest().getQuestions().size();
        attempt.setScore(score);

        return attempt;
    }

    private static boolean checkQuestion(Test test, Map.Entry<String, String[]> answerEntry) {
        long questionId = Long.parseLong(answerEntry.getKey());

        Question question = getQuestionInTest(test, questionId)
            .orElseThrow();

        Set<Long> answers = Stream.of(answerEntry.getValue())
            .map(Long::parseLong)
            .collect(Collectors.toSet());

        Set<Long> correctAnswers = question.getOptions().stream()
            .filter(Option::isCorrect)
            .map(Option::getId)
            .collect(Collectors.toSet());

        return answers.equals(correctAnswers);
    }

    private static Optional<Question> getQuestionInTest(Test test, long questionId) {
        for (Question question : test.getQuestions()) {
            if (question.getId() == questionId) {
                return Optional.of(question);
            }
        }

        return Optional.empty();
    }
}

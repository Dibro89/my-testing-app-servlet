package ua.training.mytestingapp.dao;

public interface DaoManager {

    void beginTransaction();

    UserDao createUserDao();

    TestDao createTestDao();

    QuestionDao createQuestionDao();

    OptionDao createOptionDao();

    AttemptDao createAttemptDao();

    void endTransaction();
}

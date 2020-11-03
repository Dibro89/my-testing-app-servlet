package ua.training.mytestingapp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.training.mytestingapp.dto.AttemptForm;
import ua.training.mytestingapp.entity.Attempt;
import ua.training.mytestingapp.entity.Option;
import ua.training.mytestingapp.entity.Question;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.AttemptService;
import ua.training.mytestingapp.service.UserService;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyTestingAppTest {

    static MyTestingApp myTestingApp;
    static DataSource dataSource;

    @BeforeAll
    static void setup() {
        myTestingApp = new MyTestingApp();
        dataSource = myTestingApp.initDataSource();
    }

    @Test
    void insertsUser() {
        myTestingApp.initSchema(dataSource);

        UserService userService = new UserService(dataSource);

        final String username = "hello";

        assertFalse(userService.existsByUsername(username));

        User user = new User();
        user.setUsername(username);
        user.setRegistrationDate(LocalDate.now());
        user.setRoles(new HashSet<>());

        userService.insert(user);

        assertTrue(userService.existsByUsername(username));
    }

    @Test
    void attemptServiceChecksForm() {
        // Given
        var attemptService = new AttemptService(null);
        var test = createTest();

        var form1 = createForm(test, Map.of(
            "1", new String[]{"12"},
            "2", new String[]{"21", "23"},
            "3", new String[]{"31"}
        ));

        var form2 = createForm(test, Map.of(
            "1", new String[]{"13"},
            "2", new String[]{"22"},
            "3", new String[]{"32"}
        ));

        var form3 = createForm(test, Map.of(
            "1", new String[]{"11"},
            "2", new String[]{},
            "3", new String[]{"33"}
        ));


        // When
        Attempt attempt1 = attemptService.checkAttempt(form1);
        Attempt attempt2 = attemptService.checkAttempt(form2);
        Attempt attempt3 = attemptService.checkAttempt(form3);


        // Then
        assertEquals("2/3", attempt1.getScore());
        assertEquals("0/3", attempt2.getScore());
        assertEquals("1/3", attempt3.getScore());
    }

    private static ua.training.mytestingapp.entity.Test createTest() {
        var test = new ua.training.mytestingapp.entity.Test();

        var question1 = new Question();
        question1.setId(1L);
        question1.setMultiple(false);

        var option11 = new Option();
        option11.setId(11L);
        option11.setCorrect(false);

        var option12 = new Option();
        option12.setId(12L);
        option12.setCorrect(true);

        var option13 = new Option();
        option13.setId(13L);
        option13.setCorrect(false);

        question1.setOptions(List.of(option11, option12, option13));

        var question2 = new Question();
        question2.setId(2L);
        question2.setMultiple(true);

        var option21 = new Option();
        option21.setId(21L);
        option21.setCorrect(true);

        var option22 = new Option();
        option22.setId(22L);
        option22.setCorrect(false);

        var option23 = new Option();
        option23.setId(23L);
        option23.setCorrect(true);

        question2.setOptions(List.of(option21, option22, option23));

        var question3 = new Question();
        question3.setId(3L);
        question3.setMultiple(false);

        var option31 = new Option();
        option31.setId(31L);
        option31.setCorrect(false);

        var option32 = new Option();
        option32.setId(32L);
        option32.setCorrect(false);

        var option33 = new Option();
        option33.setId(33L);
        option33.setCorrect(true);

        question3.setOptions(List.of(option31, option32, option33));

        test.setQuestions(List.of(question1, question2, question3));

        return test;
    }

    private static AttemptForm createForm(ua.training.mytestingapp.entity.Test test,
                                          Map<String, String[]> parameterMap) {
        return new AttemptForm(null, test, parameterMap);
    }
}

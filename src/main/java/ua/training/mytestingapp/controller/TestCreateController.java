package ua.training.mytestingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.Test;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.TestService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;

public class TestCreateController extends AbstractController {

    private final TestService testService;
    private final ObjectMapper objectMapper;

    public TestCreateController(TestService testService, ObjectMapper objectMapper) {
        super("/tests/create");
        this.testService = testService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        User user = getAuthenticatedUser(request);

        checkAdmin(user);

        return "test_edit";
    }

    @Override
    protected String processPost(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        Test test = requestBody(request, objectMapper, Test.class);
        test.setCreationDate(LocalDate.now());

        testService.insert(test);

        return responseBody(ctx.getResponse(), objectMapper, Map.of(
            "testId", test.getId()
        ));
    }
}

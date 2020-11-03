package ua.training.mytestingapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.Test;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.TestService;
import ua.training.mytestingapp.util.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.regex.Matcher;

public class TestEditController extends AbstractController {

    private final TestService testService;
    private final ObjectMapper objectMapper;

    public TestEditController(TestService testService, ObjectMapper objectMapper) {
        super("/tests/(?<testId>\\d+)/edit");
        this.testService = testService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        try {
            User user = getAuthenticatedUser(request);

            checkAdmin(user);

            Long testId = getLongPathVariable(matcher, "testId");

            Test test = testService.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND));

            ctx.setVariable("test", objectMapper.writeValueAsString(test));
            ctx.setVariable("testId", test.getId());

            return "test_edit";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String processPost(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        User user = getAuthenticatedUser(request);

        checkAdmin(user);

        Long testId = getLongPathVariable(matcher, "testId");

        if (!testService.existsById(testId)) {
            throw new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND);
        }

        Test test = requestBody(request, objectMapper, Test.class);
        test.setId(testId);

        testService.update(test);

        return responseBody(ctx.getResponse(), objectMapper, Map.of(
            "testId", test.getId()
        ));
    }
}

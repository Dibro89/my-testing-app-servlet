package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.TestService;
import ua.training.mytestingapp.util.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;

public class TestDeleteController extends AbstractController {

    private final TestService testService;

    public TestDeleteController(TestService testService) {
        super("/tests/(?<testId>\\d+)/delete");
        this.testService = testService;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        User user = getAuthenticatedUser(request);

        checkAdmin(user);

        Long testId = getLongPathVariable(matcher, "testId");

        if (!testService.existsById(testId)) {
            throw new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND);
        }

        testService.deleteById(testId);

        return redirect("/");
    }
}

package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.dto.AttemptForm;
import ua.training.mytestingapp.entity.Attempt;
import ua.training.mytestingapp.entity.Test;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.AttemptService;
import ua.training.mytestingapp.service.TestService;
import ua.training.mytestingapp.util.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class TestController extends AbstractController {

    private final TestService testService;
    private final AttemptService attemptService;

    public TestController(TestService testService, AttemptService attemptService) {
        super("/tests/(?<testId>\\d+)");
        this.testService = testService;
        this.attemptService = attemptService;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        User user = getAuthenticatedUser(request);

        if (user == null) {
            return redirect("/login");
        }

        Long testId = getLongPathVariable(matcher, "testId");
        String action = getOptionalParameter(request, "action").orElse("");

        Test test = testService.findById(testId)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND));

        switch (action) {
            case "begin":
                ctx.setVariable("test", test);
                return "test_inprogress";
            default:
                ctx.setVariable("testInfo", buildTestInfo(test));
                return "test";
        }
    }

    @Override
    protected String processPost(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        User user = getAuthenticatedUser(request);

        if (user == null) {
            return redirect("/login");
        }

        Long testId = getLongPathVariable(matcher, "testId");

        Test test = testService.findById(testId)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND));

        AttemptForm form = new AttemptForm(user, test, request.getParameterMap());
        Attempt attempt = attemptService.checkAttempt(form);
        attemptService.insert(attempt);

        return "test_result";
    }

    private Map<String, Object> buildTestInfo(Test test) {
        Map<String, Object> testInfo = new LinkedHashMap<>();
        testInfo.put("id", test.getId());
        testInfo.put("name", test.getName());
        testInfo.put("subject", test.getSubject());
        testInfo.put("duration", test.getDuration());
        testInfo.put("difficulty", test.getDifficulty());
        testInfo.put("creationDate", test.getCreationDate());
        return testInfo;
    }
}

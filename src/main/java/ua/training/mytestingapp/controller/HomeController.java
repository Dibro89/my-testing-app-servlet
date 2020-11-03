package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.Test;
import ua.training.mytestingapp.service.TestService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public class HomeController extends AbstractController {

    private final TestService testService;
    private static final List<String> SORTS = List.of("byName", "byDifficulty");

    public HomeController(TestService testService) {
        super("/");
        this.testService = testService;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        String subject =
            getOptionalParameter(request, "subject")
                .filter(Predicate.not(String::isBlank))
                .orElse(null);
        String sort =
            getOptionalParameter(request, "sort")
                .orElse("byName");

        List<Test> tests = testService.findAllBySubject(subject, sort);
        List<String> subjects = testService.findAllSubjects();

        ctx.setVariable("tests", tests);
        ctx.setVariable("subjects", subjects);
        ctx.setVariable("sorts", SORTS);

        return "home";
    }
}

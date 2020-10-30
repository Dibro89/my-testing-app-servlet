package ua.training.mytestingapp.controller;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.Test;
import ua.training.mytestingapp.service.TestService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HomeController implements Controller {

    private static final List<String> SORTS = List.of(
        "name", "difficulty"
    );

    private final TestService testService;

    public HomeController(TestService testService) {
        this.testService = testService;
    }

    @Override
    public void process(
        HttpServletRequest request,
        HttpServletResponse response,
        ServletContext servletContext,
        ITemplateEngine templateEngine
    ) throws Exception {
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

        Optional<String> subject = Optional.ofNullable(request.getParameter("subject"));
        String sort = Optional.ofNullable(request.getParameter("sort"))
            .filter(SORTS::contains)
            .orElse("name");

        List<Test> tests = testService.findAllBySubject(
            subject.filter(Predicate.not(String::isBlank)), sort);
        ctx.setVariable("tests", tests);

        List<String> subjects = testService.findAllSubjects();
        ctx.setVariable("subjects", subjects);

        ctx.setVariable("sorts", SORTS);

        templateEngine.process("home", ctx, response.getWriter());
    }
}

package ua.training.mytestingapp.controller;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.Test;
import ua.training.mytestingapp.service.TestService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestController implements Controller {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @Override
    public void process(
        HttpServletRequest request,
        HttpServletResponse response,
        ServletContext servletContext,
        ITemplateEngine templateEngine
    ) throws Exception {
        WebContext ctx = new WebContext(request, response, servletContext);

        Long testId = Long.parseLong(request.getParameter("testId"));

        Test test = testService.findById(testId).orElseThrow();
        ctx.setVariable("test", test);

        String action = request.getParameter("action");

        switch (action) {
            case "show":
                templateEngine.process("test", ctx, response.getWriter());
                break;
            case "begin":
                templateEngine.process("test_inprogress", ctx, response.getWriter());
                break;
            case "end":
                templateEngine.process("test_result", ctx, response.getWriter());
                break;
        }
    }
}

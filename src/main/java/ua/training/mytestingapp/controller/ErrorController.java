package ua.training.mytestingapp.controller;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorController implements Controller {

    @Override
    public void process(
        HttpServletRequest request,
        HttpServletResponse response,
        ServletContext servletContext,
        ITemplateEngine templateEngine
    ) throws Exception {
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process("error", ctx, response.getWriter());
    }
}

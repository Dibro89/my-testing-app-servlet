package ua.training.mytestingapp.controller;

import org.thymeleaf.ITemplateEngine;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserRegistrationController implements Controller {

    @Override
    public void process(
        HttpServletRequest request,
        HttpServletResponse response,
        ServletContext servletContext,
        ITemplateEngine templateEngine
    ) throws Exception {

        if (request.getMethod().equalsIgnoreCase("GET")) {
        } else {
        }

    }
}

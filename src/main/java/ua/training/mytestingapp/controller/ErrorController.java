package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;

public class ErrorController extends AbstractController {

    public ErrorController() {
        this.processAnyMethodLikeGet = true;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        return "error";
    }
}

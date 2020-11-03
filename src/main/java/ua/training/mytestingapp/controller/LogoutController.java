package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;

public class LogoutController extends AbstractController {

    public LogoutController() {
        super("/logout");
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        request.getSession().setAttribute("user", null);
        return redirect("/login");
    }
}

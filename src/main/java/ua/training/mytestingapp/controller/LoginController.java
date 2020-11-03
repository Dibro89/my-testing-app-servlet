package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(UserService userService) {
        super("/login");
        this.userService = userService;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        if (getAuthenticatedUser(request) != null) {
            return redirect("/");
        }
        return "login";
    }

    @Override
    protected String processPost(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        String username = getParameter(request, "username");
        String password = getParameter(request, "password");

        User user = userService.findByUsername(username).orElse(null);

        if (user == null || !user.getPassword().equals(password)) {
            return redirect("/login?error");
        }

        if (user.isLocked()) {
            return redirect("/login?blocked");
        }

        request.getSession().setAttribute("user", user);

        return redirect("/");
    }
}

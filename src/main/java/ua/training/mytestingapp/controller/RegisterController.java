package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.dto.UserRegistrationForm;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        super("/register");
        this.userService = userService;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        if (getAuthenticatedUser(request) != null) {
            return redirect("/");
        }

        ctx.setVariable("form", new UserRegistrationForm());

        return "user_edit";
    }

    @Override
    protected String processPost(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        UserRegistrationForm form = new UserRegistrationForm(request);
        List<String> errors = form.getErrors();

        if (!errors.isEmpty()) {
            ctx.setVariable("errors", errors);
            ctx.setVariable("form", form);
            return "user_edit";
        }

        if (userService.existsByUsername(form.getUsername())) {
            ctx.setVariable("errors", List.of("username-exists"));
            ctx.setVariable("form", form);
            return "user_edit";
        }

        User user = form.toUser();
        userService.insert(user);

        return redirect("/login?registered");
    }
}

package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.UserService;
import ua.training.mytestingapp.util.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;

public class UserBlockController extends AbstractController {

    private final UserService userService;

    public UserBlockController(UserService userService) {
        super("/users/(?<username>\\w+)/block");
        this.userService = userService;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        String username = getPathVariable(matcher, "username");
        User targetUser = userService.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND));

        User user = getAuthenticatedUser(request);
        checkAdmin(user);

        targetUser.setLocked(!targetUser.isLocked());
        userService.update(targetUser);

        return redirect("/users/" + targetUser.getUsername());
    }
}

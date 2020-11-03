package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.dto.UserEditForm;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.UserService;
import ua.training.mytestingapp.util.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Matcher;

public class UserEditController extends AbstractController {

    private final UserService userService;

    public UserEditController(UserService userService) {
        super("/users/(?<username>\\w+)/edit");
        this.userService = userService;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        String username = getPathVariable(matcher, "username");
        User targetUser = userService.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND));

        User user = getAuthenticatedUser(request);
        checkAdminOrSameUser(user, targetUser);

        ctx.setVariable("targetUser", targetUser);
        ctx.setVariable("form", new UserEditForm());

        return "user_edit";
    }

    @Override
    protected String processPost(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        String username = getPathVariable(matcher, "username");
        User targetUser = userService.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND));

        User user = getAuthenticatedUser(request);
        checkAdminOrSameUser(user, targetUser);

        UserEditForm form = new UserEditForm(request);
        List<String> errors = form.getErrors();

        if (!errors.isEmpty()) {
            ctx.setVariable("errors", errors);
            ctx.setVariable("targetUser", targetUser);
            ctx.setVariable("form", form);
            return "user_edit";
        }

        targetUser.setDisplayName(form.getDisplayName());
        targetUser.setPassword(form.getPassword());

        if (user.isAdmin()) {
            targetUser.setAdmin(form.isAdmin());
        }

        userService.update(targetUser);

        return redirect("/users/" + targetUser.getUsername());
    }

    private void checkAdminOrSameUser(User user, User targetUser) {
        if (user == null || (!user.isAdmin() && !user.getId().equals(targetUser.getId()))) {
            throw new ResponseStatusException(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}

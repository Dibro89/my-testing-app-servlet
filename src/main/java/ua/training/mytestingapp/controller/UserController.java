package ua.training.mytestingapp.controller;

import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.AttemptService;
import ua.training.mytestingapp.service.UserService;
import ua.training.mytestingapp.util.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class UserController extends AbstractController {

    private final UserService userService;
    private final AttemptService attemptService;

    public UserController(UserService userService, AttemptService attemptService) {
        super("/users/(?<username>\\w+)");
        this.userService = userService;
        this.attemptService = attemptService;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        String username = getPathVariable(matcher, "username");
        User targetUser = userService.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND));

        User user = getAuthenticatedUser(request);

        ctx.setVariable("userInfo", buildUserInfo(targetUser));
        ctx.setVariable("attempts", attemptService.findByUserId(targetUser.getId()));

        ctx.setVariable("canBlock", user != null && user.isAdmin());
        ctx.setVariable("canEdit", user != null && (user.isAdmin() || user.getId().equals(targetUser.getId())));
        ctx.setVariable("blocked", targetUser.isLocked());

        return "user";
    }

    private static Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("displayName", user.getDisplayName());
        userInfo.put("registrationDate", user.getRegistrationDate());
        return userInfo;
    }
}

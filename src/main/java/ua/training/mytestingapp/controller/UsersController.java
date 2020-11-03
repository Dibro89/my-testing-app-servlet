package ua.training.mytestingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.UserService;
import ua.training.mytestingapp.util.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public class UsersController extends AbstractController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UsersController(UserService userService, ObjectMapper objectMapper) {
        super("/users");
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected String processGet(HttpServletRequest request, WebContext ctx, Matcher matcher) {
        String accept = request.getHeader("Accept");

        if (accept == null || !accept.equals("application/json")) {
            return "users";
        }

        String username =
            getOptionalParameter(request, "username")
                .filter(Predicate.not(String::isBlank))
                .orElse(null);

        int page =
            getOptionalIntParameter(request, "page")
                .orElse(0);

        PageInfo<User> result = userService.getUserList(username, page);

        return responseBody(ctx.getResponse(), objectMapper, Map.of(
            "users", result.getContent(),
            "pagination", Map.of(
                "page", result.getNumber(),
                "totalPages", result.getTotalPages()
            )
        ));
    }
}

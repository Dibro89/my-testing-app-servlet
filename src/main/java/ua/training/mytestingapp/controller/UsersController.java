package ua.training.mytestingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.service.UserService;
import ua.training.mytestingapp.util.PageInfo;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

public class UsersController implements Controller {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void process(
        HttpServletRequest request,
        HttpServletResponse response,
        ServletContext servletContext,
        ITemplateEngine templateEngine
    ) throws Exception {

        String header = request.getHeader("Accept");
        if (header.equals("application/json")) {
            Optional<String> username = Optional.ofNullable(request.getParameter("username"));
            int page = Optional.ofNullable(request.getParameter("page")).map(Integer::parseInt).orElse(0);

            PageInfo<User> pageInfo = userService.getUserList(username, page);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), Map.of(
                "users", pageInfo.getData(),
                "pagination", Map.of(
                    "page", pageInfo.getPage(),
                    "totalPages", pageInfo.getTotalPages()
                )
            ));
            return;
        }

        WebContext ctx = new WebContext(request, response, servletContext);
        templateEngine.process("users", ctx, response.getWriter());
    }
}

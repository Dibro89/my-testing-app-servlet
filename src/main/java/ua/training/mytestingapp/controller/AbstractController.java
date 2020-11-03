package ua.training.mytestingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ua.training.mytestingapp.entity.User;
import ua.training.mytestingapp.util.ResponseStatusException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractController {

    protected static final String RESPONSE_BODY_TEMPLATE = "";
    protected static final String REDIRECT_PREFIX = "redirect:";

    protected ServletContext servletContext;
    protected TemplateEngine templateEngine;

    protected Pattern pattern;

    protected boolean processAnyMethodLikeGet = false;

    protected AbstractController() {
    }

    protected AbstractController(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public boolean process(HttpServletRequest request,
                           HttpServletResponse response) throws IOException {

        Matcher matcher;

        if (pattern != null) {
            matcher = pattern.matcher(request.getRequestURI());

            if (!matcher.matches()) {
                return false;
            }
        } else {
            matcher = null;
        }

        Locale locale = (Locale) request.getSession().getAttribute("locale");
        if (locale == null) {
            locale = Locale.US;
        }

        WebContext ctx = new WebContext(request, response, servletContext, locale);
        String template;

        if (processAnyMethodLikeGet) {

            template = processGet(request, ctx, matcher);

        } else switch (request.getMethod()) {

            case "GET":
                template = processGet(request, ctx, matcher);
                break;
            case "POST":
                template = processPost(request, ctx, matcher);
                break;
            default:
                throw new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND);

        }

        if (template.equals(RESPONSE_BODY_TEMPLATE)) {
            return true;
        }

        if (template.startsWith(REDIRECT_PREFIX)) {
            String redirectLocation = template.substring(REDIRECT_PREFIX.length());
            response.sendRedirect(redirectLocation);
            return true;
        }

        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            ctx.setVariable("user", user);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        templateEngine.process(template, ctx, response.getWriter());

        return true;
    }

    protected String processGet(HttpServletRequest request,
                                WebContext ctx,
                                Matcher matcher) {

        throw new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND);
    }

    protected String processPost(HttpServletRequest request,
                                 WebContext ctx,
                                 Matcher matcher) {

        throw new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND);
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }


    // Utility methods

    public static String redirect(String location) {
        return REDIRECT_PREFIX + location;
    }

    public static String responseBody(HttpServletResponse response, ObjectMapper objectMapper, Object obj) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(response.getWriter(), obj);
        } catch (IOException ignored) {
        }
        return RESPONSE_BODY_TEMPLATE;
    }

    public static <T> T requestBody(HttpServletRequest request, ObjectMapper objectMapper, Class<T> clazz) {
        try {
            return objectMapper.readValue(request.getReader(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Integer getIntParameter(HttpServletRequest request, String name) {
        return getOptionalIntParameter(request, name)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST));
    }

    public static Optional<Integer> getOptionalIntParameter(HttpServletRequest request, String name) {
        return getOptionalParameter(request, name).map(str -> {
            try {
                return Integer.valueOf(str);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST);
            }
        });
    }

    public static Long getLongParameter(HttpServletRequest request, String name) {
        return getOptionalLongParameter(request, name)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST));
    }

    public static Optional<Long> getOptionalLongParameter(HttpServletRequest request, String name) {
        return getOptionalParameter(request, name).map(str -> {
            try {
                return Long.valueOf(str);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST);
            }
        });
    }

    public static String getParameter(HttpServletRequest request, String name) {
        return getOptionalParameter(request, name)
            .orElseThrow(() -> new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST));
    }

    public static Optional<String> getOptionalParameter(HttpServletRequest request, String name) {
        return Optional.ofNullable(request.getParameter(name));
    }

    public static Integer getIntPathVariable(Matcher matcher, String name) {
        try {
            return Integer.valueOf(getPathVariable(matcher, name));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public static Long getLongPathVariable(Matcher matcher, String name) {
        try {
            return Long.valueOf(getPathVariable(matcher, name));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public static String getPathVariable(Matcher matcher, String name) {
        return matcher.group(name);
    }

    public static User getAuthenticatedUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    public static void checkAdmin(User user) {
        if (user == null || !user.isAdmin()) {
            throw new ResponseStatusException(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}

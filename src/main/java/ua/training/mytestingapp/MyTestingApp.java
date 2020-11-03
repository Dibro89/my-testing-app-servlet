package ua.training.mytestingapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import ua.training.mytestingapp.controller.*;
import ua.training.mytestingapp.service.AttemptService;
import ua.training.mytestingapp.service.TestService;
import ua.training.mytestingapp.service.UserService;
import ua.training.mytestingapp.util.ResourceBundleMessageResolver;
import ua.training.mytestingapp.util.ResponseStatusException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@WebFilter(displayName = "App", urlPatterns = "/*")
public class MyTestingApp implements Filter {

    private List<AbstractController> controllers;
    private AbstractController errorController;

    @Override
    public void init(FilterConfig filterConfig) {
        var servletContext = filterConfig.getServletContext();
        var templateResolver = new ServletContextTemplateResolver(servletContext);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(new ResourceBundleMessageResolver("messages"));


        // Util
        DataSource dataSource = initDataSource();
        ObjectMapper objectMapper = initObjectMapper();


        // Services
        UserService userService = new UserService(dataSource);
        TestService testService = new TestService(dataSource);
        AttemptService attemptService = new AttemptService(dataSource);


        // Initialization
        initSchema(dataSource);
        userService.createAdmin();
        testService.createSampleTest();


        // Controllers
        controllers = List.of(
            new HomeController(testService),
            new LoginController(userService),
            new LogoutController(),
            new RegisterController(userService),
            new UserController(userService, attemptService),
            new UsersController(userService, objectMapper),
            new UserEditController(userService),
            new UserBlockController(userService),
            new TestController(testService, attemptService),
            new TestCreateController(testService, objectMapper),
            new TestEditController(testService, objectMapper),
            new TestDeleteController(testService)
        );


        for (AbstractController controller : controllers) {
            controller.setServletContext(servletContext);
            controller.setTemplateEngine(templateEngine);
        }

        errorController = new ErrorController();
        errorController.setServletContext(servletContext);
        errorController.setTemplateEngine(templateEngine);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!handle(((HttpServletRequest) request), ((HttpServletResponse) response))) {
            chain.doFilter(request, response);
        }
    }

    private boolean handle(HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().startsWith("/assets")) {
            return false;
        }

        String lang = request.getParameter("lang");
        if (lang != null && (lang.equals("en") || lang.equals("ru"))) {
            Locale locale = Locale.forLanguageTag(lang);
            request.getSession().setAttribute("locale", locale);
        }

        try {

            for (AbstractController controller : controllers) {
                if (controller.process(request, response)) {
                    return true;
                }
            }

            throw new ResponseStatusException(HttpServletResponse.SC_NOT_FOUND);

        } catch (Exception e) {

            if (e instanceof ResponseStatusException) {
                response.setStatus(((ResponseStatusException) e).getStatus());
            } else {
                // TODO: log
                e.printStackTrace();

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            try {
                errorController.process(request, response);
            } catch (Exception ignored) {
            }

        }

        return true;
    }

    public void initSchema(DataSource dataSource) {
        String schemaSql = new Scanner(getClass().getResourceAsStream("/schema.sql"))
            .useDelimiter("\\A")
            .next();

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(schemaSql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DataSource initDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:h2:mem:testdb");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    public ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}

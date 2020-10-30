package ua.training.mytestingapp;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import ua.training.mytestingapp.controller.Controller;
import ua.training.mytestingapp.controller.ErrorController;
import ua.training.mytestingapp.controller.HomeController;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebFilter(displayName = "App", urlPatterns = "/*")
public class MyTestingApp implements Filter {

    private ServletContext servletContext;

    private TemplateEngine templateEngine;

    private final ErrorController errorController = new ErrorController();
    private final Map<String, Controller> controllersByUrl = Map.of(
        "/", new HomeController(testService)
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();

        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!handle(((HttpServletRequest) request), ((HttpServletResponse) response))) {
            chain.doFilter(request, response);
        }
    }

    private boolean handle(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {

            if (request.getRequestURI().startsWith("/assets")) {
                return false;
            }

            Controller controller = controllersByUrl.get(request.getRequestURI());

            if (controller == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                controller = errorController;
            }

            response.setContentType("text/html;charset=UTF-8");

            controller.process(request, response, servletContext, templateEngine);

        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IOException ignored) {
            }
            throw new ServletException(e);
        }

        return true;
    }
}

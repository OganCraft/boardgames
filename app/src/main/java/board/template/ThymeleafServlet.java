package board.template;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.io.IOException;

public class ThymeleafServlet extends HttpServlet {
    private static final String thymeleaf = "thymeleaf";

    public static void config(ServletContext context) {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        // just pass the "/page/hello" and a template "/static/thymeleaf/page/hello.th.html" will be used
        resolver.setPrefix("/static/thymeleaf/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        context.setAttribute(thymeleaf, engine);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = req.getServletContext();
        TemplateEngine engine = (TemplateEngine) servletContext.getAttribute(thymeleaf);
        WebContext webContext = new WebContext(req, resp, servletContext);

        // common response headers
        resp.setContentType("text/html;charset=UTF-8");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        String requestPath = req.getRequestURI();
        String path = requestPath.substring(3);
        engine.process(path, webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

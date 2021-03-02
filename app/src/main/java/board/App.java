package board;

import board.games.hello.HelloServlet;
import board.filter.AuthenticationFilter;
import board.games.ships.ShipsServlet;
import board.user.MemoryUserRepository;
import board.user.UserServlet;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import java.util.EnumSet;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Board Games...");

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        SessionIdManager sessionIdManager = new DefaultSessionIdManager(server);
        server.setSessionIdManager(sessionIdManager);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setBaseResource(Resource.newClassPathResource("static"));
        context.setContextPath("/");
        context.setDefaultResponseCharacterEncoding("utf-8");
        server.setHandler(context);

        context.addFilter(AuthenticationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));

        // system servlets
        context.addServlet(HelloServlet.class, "/hello");
        context.addServlet(UserServlet.class, "/user/*");

        // game servlets
        context.addServlet(ShipsServlet.class, "/ships");

        // serve static internal content - not accessible directly
        ServletHolder internalServlet = new ServletHolder("static/internal", DefaultServlet.class);
        context.addServlet(internalServlet, "/internal/*");

        // the default servlet for root content (always needed, to satisfy servlet spec)
        // must come as the last one
        ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
        defaultServlet.setInitParameter("relativeResourceBase","/web");
        defaultServlet.setInitParameter("dirAllowed","true");
        context.addServlet(defaultServlet,"/");

        // set objects shared across all servlets
        context.setAttribute("userDb", new MemoryUserRepository());

        server.start();
        server.join();
    }
}

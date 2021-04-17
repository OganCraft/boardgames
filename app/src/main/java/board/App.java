package board;

import board.filter.AuthenticationFilter;
import board.games.Games;
import board.games.chocowitch.ChocoWitchServlet;
import board.games.ships.ShipsServlet;
import board.room.RoomManager;
import board.room.RoomManagerImpl;
import board.room.RoomServlet;
import board.template.ThymeleafServlet;
import board.user.MemoryUserRepository;
import board.user.UserServlet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
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

        context.addFilter(AuthenticationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
//        context.addFilter(ThymeleafFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));

        // system servlets
        context.addServlet(UserServlet.class, "/user/*");
        context.addServlet(RoomServlet.class, "/room/*");

        // game servlets
        context.addServlet(ShipsServlet.class, Games.SHIPS.gamePath() + "/*");
        context.addServlet(ChocoWitchServlet.class, Games.CHOCO_WITCH.gamePath() + "/*");

        // html template servlet
//        context.addServlet(ThymeleafServlet.class, "/thymeleaf");
//        context.addServlet(ThymeleafServlet.class, "*.th/*");
        context.addServlet(ThymeleafServlet.class, "/th/*");

        // the default servlet for root content (always needed, to satisfy servlet spec)
        // must come as the last one
        ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
        defaultServlet.setInitParameter("relativeResourceBase", "/web");
        defaultServlet.setInitParameter("dirAllowed", "true");
        context.addServlet(defaultServlet, "/");

        // set objects shared across all servlets
        context.setAttribute("userDb", new MemoryUserRepository());
        context.setAttribute(RoomManager.roomManagerAttribute, new RoomManagerImpl());

        ErrorPageErrorHandler error = new ErrorPageErrorHandler();
        error.addErrorPage(404, "/error/404.tf");

        context.setErrorHandler(error);
        ThymeleafServlet.config(context.getServletContext());

        server.setHandler(context);

        server.start();
        server.join();
    }
}

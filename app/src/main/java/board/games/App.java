package board.games;

import board.games.hello.HelloServlet;
import board.games.server.StaticResourceHandler;
import board.games.ships.ShipsServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Board Games...");

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        ResourceHandler resourceHandler = new StaticResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("/");
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(HelloServlet.class, "/hello");
        servletHandler.addServletWithMapping(ShipsServlet.class, "/ships");

        server.setHandler(new HandlerList(
                        resourceHandler,
                        servletHandler,
                        new DefaultHandler()
                )
        );

        server.start();
        server.join();
    }
}

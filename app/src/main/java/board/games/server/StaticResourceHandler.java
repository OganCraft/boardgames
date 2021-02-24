package board.games.server;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import java.net.MalformedURLException;

public class StaticResourceHandler extends ResourceHandler {
    @Override
    public Resource getResource(String path) throws MalformedURLException {
        Resource resource = Resource.newClassPathResource("static" + path);
        return resource;
    }
}
